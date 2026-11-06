package com.mycompany.cs102.project.step1;

import java.util.*;

public class RouteFinder {

    private HashMap<String, ArrayList<Flight>> flightsFrom = new HashMap<>();

    private ArrayList<Route> results;
    private String destination;
    private int maxFlights;


    public RouteFinder(Collection<Flight> allFlights) {
        for (Flight f : allFlights) {
            if (!flightsFrom.containsKey(f.getOrigin())) {
                flightsFrom.put(f.getOrigin(), new ArrayList<Flight>());
            }
            flightsFrom.get(f.getOrigin()).add(f);
        }
    }

    public ArrayList<Route> findRoutes(String origin, String destination, int maxFlights) {
        this.results = new ArrayList<>();
        this.destination = destination;
        this.maxFlights = maxFlights;

        LinkedList<Flight> path = new LinkedList<>();
        HashSet<String> visited = new HashSet<>();
        visited.add(origin);

        search(origin, path, visited);
        // here it uses compareto from the route class
        Collections.sort(results);
        return results;
    }


    private void search(String current, LinkedList<Flight> path, HashSet<String> visited) {

        if (current.equals(destination)) {
            results.add(new Route(path));
            return;
        }

        if (path.size() == maxFlights) {
            return;
        }

        ArrayList<Flight> options = flightsFrom.get(current);
        if (options == null) {
            return;
        }

        for (Flight f : options) {
            String next = f.getDestination();

            // to not visit same airport
            if (visited.contains(next)) {
                continue;
            }

            if (!path.isEmpty()) {
                long layover = Route.minutesBetween(Route.arrivalOf(path.getLast()), f.getDepartureTime());
                if (layover < 60 || layover > 24 * 60) {
                    continue;
                }
            }

            path.addLast(f);
            visited.add(next);
            //recursive
            search(next, path, visited);

            path.removeLast();
            visited.remove(next);
        }
    }
}