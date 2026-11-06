
package com.mycompany.cs102.project.step1;
import java.util.ArrayList;
import java.util.LinkedList;

public class Route implements Comparable<Route> {

    private final ArrayList<Flight> Flights;

    public Route(LinkedList<Flight> path) {
        this.Flights = new ArrayList<>(path);
    }

    public long getTotalMinutes() {
        Flight first = Flights.get(0);
        Flight last = Flights.get(Flights.size() - 1);
        return last.getArrivalMinutes() - first.getDepartureMinutes();
    }

    @Override
    public int compareTo(Route other) {
        return Long.compare(this.getTotalMinutes(), other.getTotalMinutes());
    }

    private String formatTime(long minutes) {
        return (minutes / 60) + "h " + (minutes % 60) + "m";
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
                    + f.getDestination() + " " + f.getDeparture() + " -> " + f.getArrival());
            if (i < Flights.size() - 1) {
                long layover = Flights.get(i + 1).getDepartureMinutes() - f.getArrivalMinutes();
                System.out.println("  layover " + formatTime(layover) + " in " + f.getDestination());
            }
        }
    }

    @Override
    public String toString() {
        return "Route with " + Flights.size() + " flight(s), " + formatTime(getTotalMinutes());
    }
}