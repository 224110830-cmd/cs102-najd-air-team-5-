package com.mycompany.cs102.project.step1;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class FileManager {

    public static <T extends Storeable> void saveAll(Collection<T> items, String fileName) {
        try {
            PrintWriter out = new PrintWriter("data/" + fileName);
            for (T item : items) {
                out.println(item.toFileLine());
            }
            out.close();
        } catch (Exception e) {
            System.out.println("Could not save " + fileName);
        }
    }

 
    public static LocalDateTime makeDateTime(String text) {
        String[] parts = text.trim().replace("T", " ").split(" ");
        String[] d = parts[0].split("-");
        String[] t = parts[1].split(":");
        return LocalDateTime.of(Integer.parseInt(d[0]), Integer.parseInt(d[1]),
                Integer.parseInt(d[2]), Integer.parseInt(t[0]), Integer.parseInt(t[1]));
    }

    public static void loadFlights(Repository<Flight> flights) {
        try {
            Scanner file = new Scanner(new File("data/flights.csv"));

            while (file.hasNextLine()) {
                String line = file.nextLine();

                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    String[] p = line.split(",");

                    if (p.length != 9) {
                        System.out.println("wrong number of fields: " + line);
                        continue;
                    }

                    LocalDateTime departure = makeDateTime(p[3]);

                    Flight f = new Flight(p[0], p[1], p[2], departure,
                            Integer.parseInt(p[4]), Double.parseDouble(p[5]),
                            Integer.parseInt(p[6]), Integer.parseInt(p[7]),
                            Integer.parseInt(p[8]));

                    if (!flights.add(f)) {
                        System.out.println("Skipped (duplicate ID): " + line);
                    }
                } catch (Exception e) {
                    System.out.println("Skipped (bad line): " + line);
                }
            }
            file.close();
        } catch (Exception e) {
            System.out.println("file is missing");
        }
    }

    public static void loadPassengers(Repository<Passenger> passengers) {
        try {
            Scanner file = new Scanner(new File("data/passengers.csv"));

            while (file.hasNextLine()) {
                String line = file.nextLine();

                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    String[] p = line.split(",");

                    if (p.length != 5) {
                        System.out.println("wrong number of fields: " + line);
                        continue;
                    }

                    FrequentFlyerTier tier = FrequentFlyerTier.valueOf(p[4]);

                    Passenger passenger = new Passenger(p[0], p[1], p[2], p[3], tier);

                    // to see if emails are unique
                    boolean emailUsed = false;
                    for (Passenger other : passengers.getAll()) {
                        if (other.getEmail().equals(p[2])) {
                            emailUsed = true;
                        }
                    }

                    if (emailUsed) {
                        System.out.println("email already exists: " + line);
                    } else if (!passengers.add(passenger)) {
                        System.out.println("id already exist: " + line);
                    }
                } catch (Exception e) {
                    System.out.println("Skipped: " + line);
                }
            }
            file.close();
        } catch (Exception e) {
            System.out.println("file is missing");
        }
    }

    public static void loadBookings(Repository<Flight> flights,
                                    Repository<Passenger> passengers,
                                    Repository<Booking> bookings) {
        try {
            Scanner file = new Scanner(new File("data/bookings.csv"));

            while (file.hasNextLine()) {
                String line = file.nextLine();

                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    String[] p = line.split(",");

                    if (p.length != 6) {
                        System.out.println("Skipped (wrong number of fields): " + line);
                        continue;
                    }

                    Passenger passenger = passengers.get(p[1]);
                    Flight flight = flights.get(p[2]);
                    if (passenger == null || flight == null) {
                        System.out.println("passenger/flight is unknown: " + line);
                        continue;
                    }

                    Seat seat = flight.getSeat(p[3]);
                    if (seat == null) {
                        System.out.println("seat doesnt exist: " + line);
                        continue;
                    }

                    double price = Double.parseDouble(p[4]);
                    String status = p[5];

                    if (!status.equals("CONFIRMED") && !status.equals("CANCELLED")) {
                        System.out.println("bad status: " + line);
                        continue;
                    }

                    if (status.equals("CONFIRMED")) {
                        // check if the seat is free
                        if (seat.getIsBooked()) {
                            System.out.println("seat is already taken: " + line);
                            continue;
                        }

                        boolean alreadyHasSeat = false;
                        for (Booking other : bookings.getAll()) {
                            if (other.getPassenger().getId().equals(p[1])
                                    && other.getFlight().getId().equals(p[2])
                                    && other.getStatus().equals("CONFIRMED")) {
                                alreadyHasSeat = true;
                            }
                        }
                        if (alreadyHasSeat) {
                            System.out.println("passenger already has a seat here: " + line);
                            continue;
                        }
                    }

                    Booking booking = new Booking(p[0], passenger, flight, seat, price, status);

                    if (!bookings.add(booking)) {
                        System.out.println("same booking: " + line);
                        continue;
                    }

                    if (status.equals("CONFIRMED")) {
                        seat.book();
                    }
                } catch (Exception e) {
                    System.out.println("Skipped: " + line);
                }
            }
            file.close();
        } catch (Exception e) {
            System.out.println("no file found");
        }
    }

    public static void loadUpgrades(Repository<Booking> bookings) {
        try {
            Scanner file = new Scanner(new File("data/upgrades.csv"));

            while (file.hasNextLine()) {
                String line = file.nextLine();

                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    // p[0]=BK1008 p[1]=PLATINUM p[2]=2026-10-05 14:15:00
                    String[] p = line.split(",");

                    if (p.length != 3) {
                        System.out.println("wrong number of fields: " + line);
                        continue;
                    }

                    Booking booking = bookings.get(p[0]);
                    if (booking == null) {
                        System.out.println("unknown booking: " + line);
                        continue;
                    }

                    if (!booking.getStatus().equals("CONFIRMED")
                            || !(booking.getSeat() instanceof EconomySeat)) {
                        System.out.println("booking cant be upgraded: " + line);
                        continue;
                    }

                    FrequentFlyerTier tier = FrequentFlyerTier.valueOf(p[1]);
                    UpgradeRequest request = new UpgradeRequest(p[0], tier, p[2]);

                    booking.getFlight().addUpgradeRequest(request);
                } catch (Exception e) {
                    System.out.println("Skipped: " + line);
                }
            }
            file.close();
        } catch (Exception e) {
            System.out.println("no file found");
        }
    }
}