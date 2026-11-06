package com.mycompany.cs102.project.step1;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;

public class Route implements Comparable<Route> {

    private final ArrayList<Flight> Flights;

    public Route(LinkedList<Flight> path) {
        this.Flights = new ArrayList<>(path);
    }

    public static LocalDateTime arrivalOf(Flight f) {
        return f.getDepartureTime().plusMinutes(f.getDurationInMinutes());
    }

    public static long minutesBetween(LocalDateTime from, LocalDateTime to) {
        return Duration.between(from, to).toMinutes();
    }

    public long getTotalMinutes() {
        Flight first = Flights.get(0);
        Flight last = Flights.get(Flights.size() - 1);
        return minutesBetween(first.getDepartureTime(), arrivalOf(last));
    }

    @Override
    public int compareTo(Route other) {
        return Long.compare(this.getTotalMinutes(), other.getTotalMinutes());
    }

    private String formatTime(long minutes) {
        return (minutes / 60) + "h " + (minutes % 60) + "m";
    }

    private String formatDate(LocalDateTime time) {
        return time.toString().replace("T", " ");
    }

    public void print(int number) {
        String cities = Flights.get(0).getOrigin();
        for (Flight f : Flights) {
            cities = cities + " -> " + f.getDestination();
        }
        System.out.println("Option " + number + ": " + cities + " | "
                + (Flights.size() - 1) + " stop(s) | total " + formatTime(getTotalMinutes()));

        for (int i = 0; i < Flights.size(); i++) {
            Flight f = Flights.get(i);
            System.out.println("  " + f.getFlightNumber() + " " + f.getOrigin() + " -> "
                    + f.getDestination() + " " + formatDate(f.getDepartureTime())
                    + " -> " + formatDate(arrivalOf(f)));
            if (i < Flights.size() - 1) {
                long layover = minutesBetween(arrivalOf(f), Flights.get(i + 1).getDepartureTime());
                System.out.println("  layover " + formatTime(layover) + " in " + f.getDestination());
            }
        }
    }

    @Override
    public String toString() {
        return "Route with " + Flights.size() + " flight(s), " + formatTime(getTotalMinutes());
    }
}