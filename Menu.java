package com.mycompany.cs102.project.step1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Menu {

    private Scanner input = new Scanner(System.in);
    private Repository<Flight> flights = new Repository<>();
    private Repository<Passenger> passengers = new Repository<>();
    private Repository<Booking> bookings = new Repository<>();



 
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
                System.out.println("Book a seat: connect to Member 2's code.");
            } else if (choice == 9) {
                System.out.println("View bookings: connect to Member 2's code.");
            } else if (choice == 10) {
                System.out.println("Cancel a booking: connect to Member 2's code.");
            } else if (choice == 11) {
                System.out.println("Request upgrade: connect to Member 2's code.");
            } else if (choice == 12) {
                System.out.println("View waitlist: connect to Member 2's code.");
            } else if (choice == 13) {
                System.out.println("Process upgrades: connect to Member 2's code.");
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

    // F1 
    private void listFlights() {
        ArrayList<Flight> list = flights.getAll();
        Collections.sort(list); 
        if (list.isEmpty()) {
            System.out.println("No flights yet.");
        }
        for (Flight f : list) {
            System.out.println(f); 
        }
    }

    //F2 
    private void searchDirectFlights() {
        String from = readText("From (airport code): ").toUpperCase();
        String to = readText("To (airport code): ").toUpperCase();
        boolean found = false;
        for (Flight f : flights.getAll()) {
            if (f.getOrigin().equals(from) && f.getDestination().equals(to)) {
                System.out.println(f);
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
                // the Passenger constructor rejected a bad value
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
                Flight f = new Flight(number, from, to, departure, duration, fare,
                        first, business, economy);
                if (flights.add(f)) {
                    saveData();
                    System.out.println("Flight added.");
                    return;
                }
                System.out.println("That flight number already exists. Try again.");
            } catch (Exception e) {
                System.out.println("Invalid data: " + e.getMessage() + ". Try again.");
            }
        }
    }
}