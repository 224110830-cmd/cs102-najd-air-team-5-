package com.mycompany.cs102.project.step1;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Menu {

    private Scanner input = new Scanner(System.in);
    private Repository<Flight> flights = new Repository<>();
    private Repository<Passenger> passengers = new Repository<>();
    private Repository<Booking> bookings = new Repository<>();
    // MEMBER 2: the booking rules. It must stay BELOW the three repositories above.
    private BookingService service = new BookingService(flights, passengers, bookings);


    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String text = input.nextLine().trim();
            try {
                int number = Integer.parseInt(text);
                if (number >= min && number <= max) {
                    return number;
                }
                System.out.println("Please enter a number from " + min + " to " + max + ".");
            } catch (Exception e) {
                System.out.println("That is not a number. Try again.");
            }
        }
    }


    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = input.nextLine().trim();
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("This cannot be empty.");
        }
    }


    private boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String text = input.nextLine().trim().toLowerCase();
            if (text.equals("y")) {
                return true;
            }
            if (text.equals("n")) {
                return false;
            }
            System.out.println("Please type y or n.");
        }
    }


    private FrequentFlyerTier readTier() {
        System.out.println("1. PLATINUM  2. GOLD  3. SILVER  4. NONE");
        int choice = readInt("Tier: ", 1, 4);
        if (choice == 1) {
            return FrequentFlyerTier.PLATINUM;
        } else if (choice == 2) {
            return FrequentFlyerTier.GOLD;
        } else if (choice == 3) {
            return FrequentFlyerTier.SILVER;
        } else {
            return FrequentFlyerTier.NONE;
        }
    }


    private void loadData() {
        FileManager.loadFlights(flights);
        FileManager.loadPassengers(passengers);
        FileManager.loadBookings(flights, passengers, bookings);
        FileManager.loadUpgrades(bookings);
    }

    // any changes to the data folder call this method
    private void saveData() {
        FileManager.saveAll(flights.getAll(), "flights.csv");
        FileManager.saveAll(passengers.getAll(), "passengers.csv");
        FileManager.saveAll(bookings.getAll(), "bookings.csv");

        ArrayList<UpgradeRequest> allRequests = new ArrayList<>();
        for (Flight f : flights.getAll()) {
            allRequests.addAll(f.getUpgradeRequests());
        }
        FileManager.saveAll(allRequests, "upgrades.csv");
    }

    public void run() {
        loadData();
        int choice = -1;
        while (choice != 0) {
            printMenu();
            choice = readInt("Choose an option: ", 0, 14);

            if (choice == 1) {
                listFlights();
            } else if (choice == 2) {
                searchDirectFlights();
            } else if (choice == 3) {
                findConnectingRoutes();
            } else if (choice == 4) {
                viewSeatMap();
            } else if (choice == 5) {
                registerPassenger();
            } else if (choice == 6) {
                listPassengers();
            } else if (choice == 7) {
                updatePassenger();
            } else if (choice == 8) {
                bookSeat();
            } else if (choice == 9) {
                viewPassengerBookings();
            } else if (choice == 10) {
                cancelBooking();
            } else if (choice == 11) {
                requestUpgrade();
            } else if (choice == 12) {
                viewWaitlist();
            } else if (choice == 13) {
                processUpgrades();
            } else if (choice == 14) {
                addFlight();
            }
        }
        saveData();
        System.out.println("Data saved. Goodbye!");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=============== NAJD AIR BOOKING SYSTEM ===============");
        System.out.println("FLIGHTS     1. List all flights");
        System.out.println("            2. Search direct flights");
        System.out.println("            3. Find connecting routes");
        System.out.println("            4. View seat map");
        System.out.println("PASSENGERS  5. Register a passenger");
        System.out.println("            6. List passengers");
        System.out.println("            7. Update passenger details");
        System.out.println("BOOKINGS    8. Book a seat");
        System.out.println("            9. View a passenger's bookings");
        System.out.println("            10. Cancel a booking");
        System.out.println("UPGRADES    11. Request a business upgrade");
        System.out.println("            12. View upgrade waitlist");
        System.out.println("            13. Process upgrades");
        System.out.println("ADMIN       14. Add a new flight");
        System.out.println("            0. Save and exit");
    }

    // prints one flight: route, times, duration, fare and free seats per cabin
    private void printFlight(Flight f) {
        LocalDateTime arrival = Route.arrivalOf(f);
        int hours = f.getDurationInMinutes() / 60;
        int minutes = f.getDurationInMinutes() % 60;

        int firstFree = 0;
        int businessFree = 0;
        int economyFree = 0;
        for (Seat s : f.getSeats()) {
            if (!s.getIsBooked()) {
                if (s instanceof FirstClassSeat) {
                    firstFree++;
                } else if (s instanceof BusinessSeat) {
                    businessFree++;
                } else {
                    economyFree++;
                }
            }
        }

        System.out.println(f.getFlightNumber() + " " + f.getOrigin() + " -> " + f.getDestination()
                + "  " + f.getDepartureTime().toString().replace("T", " ")
                + " -> " + arrival.toString().replace("T", " ")
                + "  (" + hours + "h " + minutes + "m)  " + f.getBaseFare() + " SAR");
        System.out.println("    Free seats - First: " + firstFree + " of " + f.getFirstClassSeatCount()
                + ", Business: " + businessFree + " of " + f.getBusinessSeatCount()
                + ", Economy: " + economyFree + " of " + f.getEconomySeatCount());
    }

    // F1
    private void listFlights() {
        ArrayList<Flight> list = new ArrayList<>(flights.getAll());
        Collections.sort(list);
        if (list.isEmpty()) {
            System.out.println("No flights yet.");
        }
        for (Flight f : list) {
            printFlight(f);
        }
    }

    //F2
    private void searchDirectFlights() {
        String from = readText("From (airport code): ").toUpperCase();
        String to = readText("To (airport code): ").toUpperCase();
        boolean found = false;
        for (Flight f : flights.getAll()) {
            if (f.getOrigin().equals(from) && f.getDestination().equals(to)) {
                printFlight(f);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No direct flights found.");
        }
    }

    // F3
    private void findConnectingRoutes() {
        String from = readText("From: ").toUpperCase();
        String to = readText("To: ").toUpperCase();
        if (from.equals(to)) {
            System.out.println("Origin and destination must be different.");
            return;
        }
        int max = readInt("Maximum number of flights in the trip (1-4): ", 1, 4);

        RouteFinder finder = new RouteFinder(flights.getAll());
        ArrayList<Route> routes = finder.findRoutes(from, to, max);

        System.out.println(routes.size() + " route(s) found, fastest first.");
        int number = 1;
        for (Route r : routes) {
            r.print(number);
            number++;
        }
    }

    // F4
    private void viewSeatMap() {
        String number = readText("Flight number: ").toUpperCase();
        Flight f = flights.get(number);
        if (f == null) {
            System.out.println("No such flight.");
        } else {
            f.printSeatMap();
        }
    }

    //F5
    private void registerPassenger() {
        while (true) {
            String name = readText("Full name: ");
            String email = readText("Email: ");
            String phone = readText("Mobile (05xxxxxxxx): ");
            FrequentFlyerTier tier = readTier();

            // unique email
            boolean used = false;
            for (Passenger p : passengers.getAll()) {
                if (p.getEmail().equals(email)) {
                    used = true;
                }
            }
            if (used) {
                System.out.println("That email is already registered. Try again.");
                continue;
            }

            try {
                String id = "P" + (1001 + passengers.size());
                Passenger p = new Passenger(id, name, email, phone, tier);
                passengers.add(p);
                saveData();
                System.out.println("Registered. Passenger ID: " + id);
                return;
            } catch (Exception e) {
                System.out.println("Invalid data: " + e.getMessage() + ". Try again.");
            }
        }
    }

    //F6
    private void listPassengers() {
        if (passengers.size() == 0) {
            System.out.println("No passengers yet.");
        }
        for (Passenger p : passengers.getAll()) {
            System.out.println(p);
        }
    }

    // F7
    private void updatePassenger() {
        String id = readText("Passenger ID: ").toUpperCase();
        Passenger p = passengers.get(id);
        if (p == null) {
            System.out.println("No such passenger.");
            return;
        }
        System.out.println("1. Email  2. Mobile  3. Tier");
        int what = readInt("What to change: ", 1, 3);
        try {
            if (what == 1) {
                p.setEmail(readText("New email: "));
            } else if (what == 2) {
                p.setPhone(readText("New mobile: "));
            } else {
                p.setTier(readTier());
            }
            saveData();
            System.out.println("Updated.");
        } catch (Exception e) {
            System.out.println("Invalid data: " + e.getMessage());
        }
    }

    // =====================================================================
    //  MEMBER 2: F8 to F13. The menu only asks and prints;
    //  every rule is checked inside BookingService.
    // =====================================================================

    // F8
    private void bookSeat() {
        Passenger p = passengers.get(readText("Passenger ID: ").toUpperCase());
        if (p == null) {
            System.out.println("No such passenger.");
            return;
        }
        Flight f = flights.get(readText("Flight number: ").toUpperCase());
        if (f == null) {
            System.out.println("No such flight.");
            return;
        }
        if (service.hasConfirmedBooking(p, f)) {
            System.out.println(p.getFullName() + " already has a confirmed seat on this flight.");
            return;
        }

        // show the three cabins: free seats and the price for THIS passenger
        System.out.println(f.getFlightNumber() + " " + f.getOrigin() + " -> " + f.getDestination()
                + " " + f.getDepartureTime().toString().replace("T", " "));
        Cabin[] cabins = {Cabin.FIRST_cLASS, Cabin.BUSINESS_CLASS, Cabin.ECONOMY_CLASS};
        String[] names = {"First", "Business", "Economy"};
        int[] totals = {f.getFirstClassSeatCount(), f.getBusinessSeatCount(), f.getEconomySeatCount()};
        for (int i = 0; i < 3; i++) {
            double price = Math.round(f.getBaseFare() * cabins[i].getPriceMultiplyer()
                    * (1.0 - p.getTier().getDiscount()) * 100.0) / 100.0;
            System.out.println("  " + (i + 1) + ". " + names[i] + " "
                    + service.getFreeSeats(f, cabins[i]).size() + " of " + totals[i] + " free  "
                    + String.format("%,.2f", price) + " SAR");
        }

        int choice = readInt("Cabin: ", 1, 3);
        Cabin cabin = cabins[choice - 1];
        ArrayList<Seat> free = service.getFreeSeats(f, cabin);
        if (free.isEmpty()) {
            System.out.println("No free seats in " + names[choice - 1] + ".");
            return;
        }

        System.out.print("Free " + names[choice - 1] + " seats:");
        for (Seat s : free) {
            System.out.print(" " + s.getSeatNumber());
        }
        System.out.println();

        // choose the seat: Enter = first free seat
        Seat seat = null;
        while (seat == null) {
            System.out.print("Seat number (Enter = first free seat, " + free.get(0).getSeatNumber() + "): ");
            String text = input.nextLine().trim().toUpperCase();
            if (text.isEmpty()) {
                seat = free.get(0);
            } else if (free.contains(f.getSeat(text))) {
                seat = f.getSeat(text);
            } else {
                System.out.println("That is not a free seat in this cabin. Try again.");
            }
        }

        double price = seat.calculatePrice(f.getBaseFare(), p.getTier());
        System.out.println("Seat " + seat.getSeatNumber() + " (" + names[choice - 1] + "): "
                + seat.getPerks() + ". Baggage " + seat.getBaggageAllowanceKg() + " kg.");
        System.out.println("Price: " + String.format("%,.2f", price) + " SAR ("
                + p.getTier() + " discount " + (int) Math.round(p.getTier().getDiscount() * 100) + "%)");

        if (!readYesNo("Confirm booking?")) {
            System.out.println("Booking cancelled. Nothing was saved.");
            return;
        }
        try {
            Booking b = service.bookSeat(p, f, seat);
            saveData();
            System.out.println("Booking confirmed. Reference: " + b.getBookingRef());
        } catch (BookingException e) {
            System.out.println("Could not book: " + e.getMessage());
        }
    }

    // F9
    private void viewPassengerBookings() {
        Passenger p = passengers.get(readText("Passenger ID: ").toUpperCase());
        if (p == null) {
            System.out.println("No such passenger.");
            return;
        }
        ArrayList<Booking> list = service.getBookingsOf(p);
        if (list.isEmpty()) {
            System.out.println("No bookings for " + p.getFullName() + ".");
            return;
        }
        for (Booking b : list) {
            System.out.println(b);
        }
        System.out.println("Total paid (confirmed bookings): "
                + String.format("%,.2f", service.getTotalPaid(list)) + " SAR");
    }

    // F10
    private void cancelBooking() {
        String ref = readText("Booking reference: ").toUpperCase();
        try {
            service.cancelBooking(ref);
            saveData();
            System.out.println("Booking " + ref + " cancelled. The seat is free again.");
        } catch (BookingException e) {
            System.out.println("Could not cancel: " + e.getMessage());
        }
    }

    // F11
    private void requestUpgrade() {
        String ref = readText("Booking reference: ").toUpperCase();
        try {
            int position = service.requestUpgrade(ref);
            saveData();
            System.out.println("Request added. Your position in the waitlist: " + position);
        } catch (BookingException e) {
            System.out.println("Could not request upgrade: " + e.getMessage());
        }
    }

    // F12
    private void viewWaitlist() {
        Flight f = flights.get(readText("Flight number: ").toUpperCase());
        if (f == null) {
            System.out.println("No such flight.");
            return;
        }
        ArrayList<UpgradeRequest> line = f.getWaitlistInOrder();
        if (line.isEmpty()) {
            System.out.println("The waitlist for " + f.getFlightNumber() + " is empty.");
            return;
        }
        System.out.println("Business free seats: "
                + service.getFreeSeats(f, Cabin.BUSINESS_CLASS).size());
        int position = 1;
        for (UpgradeRequest r : line) {
            Booking b = bookings.get(r.getBookingRef());
            String name = (b == null) ? "?" : b.getPassenger().getFullName();
            System.out.println(position + ". " + r.getBookingRef() + " | " + name
                    + " | " + r.getTierAtRequest() + " | requested "
                    + r.getRequestedAt().toString().replace("T", " "));
            position++;
        }
    }

    // F13
    private void processUpgrades() {
        Flight f = flights.get(readText("Flight number: ").toUpperCase());
        if (f == null) {
            System.out.println("No such flight.");
            return;
        }
        ArrayList<Booking> done = service.processUpgrades(f);
        saveData();
        if (done.isEmpty()) {
            System.out.println("No upgrades were made (empty waitlist or business class is full).");
        }
        for (Booking b : done) {
            System.out.println("Upgraded " + b.getBookingRef() + " (" + b.getPassenger().getFullName()
                    + ") to business seat " + b.getSeat().getSeatNumber());
        }
        System.out.println("Still waiting: " + f.getUpgradeRequests().size());
    }

    //F14
    private void addFlight() {
        while (true) {
            String number = readText("Flight number (e.g. NA101): ").toUpperCase();
            String from = readText("Origin code: ").toUpperCase();
            String to = readText("Destination code: ").toUpperCase();
            String departure = readText("Departure (yyyy-MM-dd HH:mm): ");
            int duration = readInt("Duration in minutes (30-1200): ", 30, 1200);
            int fare = readInt("Base fare in SAR (1-20000): ", 1, 20000);
            int first = readInt("First class seats (0-20): ", 0, 20);
            int business = readInt("Business seats (0-60): ", 0, 60);
            int economy = readInt("Economy seats (1-300): ", 1, 300);

            try {
                LocalDateTime time = FileManager.makeDateTime(departure);
                Flight f = new Flight(number, from, to, time, duration, fare,
                        first, business, economy);
                if (flights.add(f)) {
                    saveData();
                    System.out.println("Flight added.");
                    return;
                }
                System.out.println("That flight number already exists. Try again.");
            } catch (Exception e) {
                System.out.println("Invalid data, check every value and the date format. Try again.");
            }
        }
    }
}