package com.mycompany.cs102.project.step1;

import java.util.Locale;
import java.util.Objects;

/**
 * One booking: it links a Passenger, a Flight and a Seat (ASSOCIATION).
 * It does not own them; it only points to them.
 *
 * Booking implements Storeable, so it can be saved by FileManager.saveAll().
 * A cancelled booking is NOT deleted: we keep it with status CANCELLED.
 *
 * @author Member 2
 */
public class Booking implements Storeable {

    public static final String CONFIRMED = "CONFIRMED";
    public static final String CANCELLED = "CANCELLED";

    // final = can never change after the object is created (no setter)
    private final String bookingRef;
    private final Passenger passenger;
    private final Flight flight;
    private final double pricePaid;

    // these two CAN change: the seat changes on an upgrade, the status on a cancel
    private Seat seat;
    private String status;

    /**
     * FileManager calls exactly this constructor:
     * new Booking(ref, passenger, flight, seat, price, status)
     * Every value is validated, so an invalid Booking can never exist,
     * even if someone hand-edits bookings.csv.
     */
    public Booking(String bookingRef, Passenger passenger, Flight flight,
                   Seat seat, double pricePaid, String status) throws InvalidDataException {
        if (bookingRef == null || !bookingRef.matches("^BK\\d{4,}$")) {
            throw new InvalidDataException("Invalid booking reference: must be BK followed by digits (e.g., BK1001).");
        }
        if (passenger == null) {
            throw new InvalidDataException("A booking needs a passenger.");
        }
        if (flight == null) {
            throw new InvalidDataException("A booking needs a flight.");
        }
        if (pricePaid < 0) {
            throw new InvalidDataException("Price paid cannot be negative.");
        }
        this.bookingRef = bookingRef;
        this.passenger = passenger;
        this.flight = flight;
        this.pricePaid = pricePaid;
        setSeat(seat);      // reuse the setter so the seat check is written only once
        setStatus(status);  // same for the status check
    }

    // ---------- getters (no setter for bookingRef, passenger, flight, price) ----------

    public String getBookingRef() {
        return bookingRef;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public Seat getSeat() {
        return seat;
    }

    public double getPricePaid() {
        return pricePaid;
    }

    public String getStatus() {
        return status;
    }

    // ---------- setters that validate BEFORE changing anything ----------

    public void setSeat(Seat seat) throws InvalidDataException {
        if (seat == null) {
            throw new InvalidDataException("A booking needs a seat.");
        }
        this.seat = seat;
    }

    private void setStatus(String status) throws InvalidDataException {
        if (!CONFIRMED.equals(status) && !CANCELLED.equals(status)) {
            throw new InvalidDataException("Invalid status: must be CONFIRMED or CANCELLED.");
        }
        this.status = status;
    }

    // ---------- behaviour ----------

    public boolean isConfirmed() {
        return CONFIRMED.equals(status);
    }

    /** Marks the booking as cancelled. (Freeing the seat is done by BookingService.) */
    public void cancel() {
        this.status = CANCELLED;
    }

    /** "First", "Business" or "Economy", found from the real type of the seat. */
    public String getCabinName() {
        if (seat instanceof FirstClassSeat) {
            return "First";
        } else if (seat instanceof BusinessSeat) {
            return "Business";
        }
        return "Economy";
    }

    // ---------- Storeable (the interface shared with Flight and Passenger) ----------

    @Override
    public String getId() {
        return bookingRef;
    }

    /**
     * One CSV line, in the same order as bookings.csv:
     * bookingRef,passengerId,flightNumber,seatNumber,pricePaid,status
     * Locale.US makes sure the price is written as 382.50 and never 382,50.
     */
    @Override
    public String toFileLine() {
        return bookingRef + "," + passenger.getId() + "," + flight.getFlightNumber() + ","
                + seat.getSeatNumber() + "," + String.format(Locale.US, "%.2f", pricePaid)
                + "," + status;
    }

    // ---------- overriding Object methods ----------

    @Override
    public String toString() {
        return bookingRef + " | " + passenger.getFullName()
                + " | " + flight.getFlightNumber() + " " + flight.getOrigin() + " -> " + flight.getDestination()
                + " | " + flight.getDepartureTime().toString().replace("T", " ")
                + " | Seat " + seat.getSeatNumber() + " (" + getCabinName() + ")"
                + " | " + String.format(Locale.US, "%.2f", pricePaid) + " SAR"
                + " | " + status;
    }

    /** Two bookings are the same booking when they have the same reference. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Booking other = (Booking) o;
        return Objects.equals(this.bookingRef, other.bookingRef);
    }

    /** MUST use the same field as equals(): equal objects need equal hash codes. */
    @Override
    public int hashCode() {
        return Objects.hash(bookingRef);
    }
}
