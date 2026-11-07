package com.mycompany.cs102.project.step1;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * The booking rules (F8 to F13). The menu only asks questions and prints;
 * every rule is checked here.
 *
 * It uses the three Repository objects (HashMap inside) to find things by ID.
 *
 * @author Member 2
 */
public class BookingService {

    private final Repository<Flight> flights;
    private final Repository<Passenger> passengers;
    private final Repository<Booking> bookings;

    public BookingService(Repository<Flight> flights, Repository<Passenger> passengers,
                          Repository<Booking> bookings) {
        this.flights = flights;
        this.passengers = passengers;
        this.bookings = bookings;
    }

    // =====================================================================
    //  Helpers
    // =====================================================================

    /** Next reference: the biggest number used so far + 1 (BK1016 -> BK1017). */
    public String nextBookingRef() {
        int max = 1000;
        for (Booking b : bookings.getAll()) {
            int number = Integer.parseInt(b.getBookingRef().substring(2));
            if (number > max) {
                max = number;
            }
        }
        return "BK" + (max + 1);
    }

    /** True if this seat belongs to this cabin (uses the real class of the seat). */
    public boolean isInCabin(Seat seat, Cabin cabin) {
        if (cabin == Cabin.FIRST_cLASS) {
            return seat instanceof FirstClassSeat;
        } else if (cabin == Cabin.BUSINESS_CLASS) {
            return seat instanceof BusinessSeat;
        }
        return seat instanceof EconomySeat;
    }

    /** All free seats of one cabin, in seat-map order. */
    public ArrayList<Seat> getFreeSeats(Flight flight, Cabin cabin) {
        ArrayList<Seat> free = new ArrayList<>();
        for (Seat s : flight.getSeats()) {
            if (isInCabin(s, cabin) && !s.getIsBooked()) {
                free.add(s);
            }
        }
        return free;
    }

    /**
     * Does this passenger already hold a CONFIRMED seat on this flight?
     * We put the passenger IDs of all confirmed bookings on the flight into a
     * HashSet. A HashSet has no duplicates and contains() is fast.
     * A cancelled booking is not added, so it never blocks a new booking.
     */
    public boolean hasConfirmedBooking(Passenger passenger, Flight flight) {
        HashSet<String> passengersOnFlight = new HashSet<>();
        for (Booking b : bookings.getAll()) {
            if (b.isConfirmed() && b.getFlight().equals(flight)) {
                passengersOnFlight.add(b.getPassenger().getId());
            }
        }
        return passengersOnFlight.contains(passenger.getId());
    }

    // =====================================================================
    //  F8  Book a seat
    // =====================================================================

    public Booking bookSeat(Passenger passenger, Flight flight, Seat seat) throws BookingException {
        if (seat == null) {
            throw new BookingException("That seat does not exist on this flight.");
        }
        if (seat.getIsBooked()) {
            throw new BookingException("Seat " + seat.getSeatNumber() + " is already taken.");
        }
        if (hasConfirmedBooking(passenger, flight)) {
            throw new BookingException(passenger.getFullName()
                    + " already has a confirmed seat on flight " + flight.getFlightNumber() + ".");
        }

        double price = seat.calculatePrice(flight.getBaseFare(), passenger.getTier());

        try {
            // Create the Booking FIRST. If anything is invalid it throws here,
            // and the seat has not been touched yet.
            Booking booking = new Booking(nextBookingRef(), passenger, flight, seat,
                    price, Booking.CONFIRMED);
            bookings.add(booking);
            seat.book();
            return booking;
        } catch (InvalidDataException e) {
            throw new BookingException(e.getMessage());
        }
    }

    // =====================================================================
    //  F9  A passenger's bookings
    // =====================================================================

    /** All bookings of one passenger (cancelled too), sorted by departure time. */
    public ArrayList<Booking> getBookingsOf(Passenger passenger) {
        ArrayList<Booking> result = new ArrayList<>();
        for (Booking b : bookings.getAll()) {
            if (b.getPassenger().equals(passenger)) {
                result.add(b);
            }
        }
        Collections.sort(result, new Comparator<Booking>() {
            @Override
            public int compare(Booking a, Booking b) {
                return a.getFlight().getDepartureTime().compareTo(b.getFlight().getDepartureTime());
            }
        });
        return result;
    }

    /** Total paid, counting CONFIRMED bookings only. */
    public double getTotalPaid(List<Booking> list) {
        double total = 0;
        for (Booking b : list) {
            if (b.isConfirmed()) {
                total += b.getPricePaid();
            }
        }
        return total;
    }

    // =====================================================================
    //  F10  Cancel a booking
    // =====================================================================

    public void cancelBooking(String bookingRef) throws BookingException {
        Booking booking = bookings.get(bookingRef);   // HashMap lookup
        if (booking == null) {
            throw new BookingException("No booking with reference " + bookingRef + ".");
        }
        if (!booking.isConfirmed()) {
            throw new BookingException("Booking " + bookingRef + " is already cancelled.");
        }
        // 1. remove its pending upgrade request (if any)
        booking.getFlight().removeUpgradeRequest(bookingRef);
        // 2. free the seat immediately
        booking.getSeat().release();
        // 3. keep the booking as a record, with status CANCELLED
        booking.cancel();
    }

    // =====================================================================
    //  F11  Request an upgrade
    // =====================================================================

    /** Returns the position in line (1 = next to be upgraded). */
    public int requestUpgrade(String bookingRef) throws BookingException {
        Booking booking = bookings.get(bookingRef);
        if (booking == null) {
            throw new BookingException("No booking with reference " + bookingRef + ".");
        }
        if (!booking.isConfirmed()) {
            throw new BookingException("Only a confirmed booking can be upgraded.");
        }
        if (!(booking.getSeat() instanceof EconomySeat)) {
            throw new BookingException("Only economy bookings can be upgraded.");
        }
        Flight flight = booking.getFlight();
        if (flight.getBusinessSeatCount() == 0) {
            throw new BookingException("Flight " + flight.getFlightNumber() + " has no business cabin.");
        }

        try {
            // The tier is saved NOW (tierAtRequest). A later tier change will not move it.
            UpgradeRequest request = new UpgradeRequest(bookingRef,
                    booking.getPassenger().getTier(), LocalDateTime.now());
            if (!flight.addUpgradeRequest(request)) {
                throw new BookingException("Booking " + bookingRef + " is already on the waitlist.");
            }
        } catch (InvalidDataException e) {
            throw new BookingException(e.getMessage());
        }

        ArrayList<UpgradeRequest> line = flight.getWaitlistInOrder();
        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).getBookingRef().equals(bookingRef)) {
                return i + 1;
            }
        }
        return line.size();
    }

    // =====================================================================
    //  F13  Process upgrades
    // =====================================================================

    /**
     * Gives free business seats to the waitlist, in priority order, until the
     * waitlist is empty or business class is full.
     * Returns the bookings that were upgraded. The price paid does not change.
     */
    public ArrayList<Booking> processUpgrades(Flight flight) {
        ArrayList<Booking> upgraded = new ArrayList<>();

        while (!flight.getUpgradeRequests().isEmpty()) {
            ArrayList<Seat> freeBusiness = getFreeSeats(flight, Cabin.BUSINESS_CLASS);
            if (freeBusiness.isEmpty()) {
                break;                                  // business is full: stop
            }
            // poll() removes and returns the request at the FRONT of the queue
            UpgradeRequest request = flight.getUpgradeRequests().poll();
            Booking booking = bookings.get(request.getBookingRef());
            if (booking == null || !booking.isConfirmed()) {
                continue;                               // stale request: skip it
            }

            Seat oldSeat = booking.getSeat();
            Seat newSeat = freeBusiness.get(0);
            try {
                booking.setSeat(newSeat);               // may throw, so do it first
                newSeat.book();                         // take the new seat ...
                oldSeat.release();                      // ... AND free the old one
                upgraded.add(booking);
            } catch (InvalidDataException e) {
                // cannot happen (newSeat is never null), but then nothing was changed
            }
        }
        return upgraded;
    }
}
