/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cs102.project.step1;
import java.util.*;
import java.time.LocalDateTime;

/**
 *
 * @author saleh
 */
public class Flight implements Storeable, Comparable<Flight> {
    private String flightNumber; 
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private int durationInMinutes;
    private double baseFare;
    private int firstClassSeatCount;
    private int businessSeatCount;
    private int economySeatCount;
    private ArrayList<Seat> seats= new ArrayList<>();

    public Flight(String flightNumber, String origin, String destination, LocalDateTime departureTime, int durationInMinutes, double baseFare, int firstClassSeatCount, int businessSeatCount, int economySeatCount) throws InvalidDataException{
        setFlightNumber(flightNumber);
        setOrigin(origin);
        setDestination(destination);
        setDepartureTime(departureTime);
        setDurationInMinutes(durationInMinutes);
        setBaseFare(baseFare);
        setFirstClassSeatCount(firstClassSeatCount);
        setBusinessSeatCount(businessSeatCount);
        setEconomySeatCount(economySeatCount);
        generateSeats();
        
    }

    
    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public int getFirstClassSeatCount() {
        return firstClassSeatCount;
    }

    public int getBusinessSeatCount() {
        return businessSeatCount;
    }

    public int getEconomySeatCount() {
        return economySeatCount;
    }

    public ArrayList<Seat> getSeats() {
        return seats;
    }

    public void setFlightNumber(String flightNumber) throws InvalidDataException{
        if(!flightNumber.matches("^[A-Z]{2}\\d{3,4}")) 
        {throw new InvalidDataException("Invalid Flight number! a flight number must be two upper-case letters followed by 3-4 digits");}
        this.flightNumber = flightNumber;
    }

    public void setOrigin(String origin) throws InvalidDataException{
if(!origin.matches("^[A-Z]{3}")||origin.equalsIgnoreCase(destination))
{throw new InvalidDataException("Invalid Origin! an origin consist of 3 Uppercase Letters, and not equal the destination");}
        this.origin = origin;
    }

    public void setDestination(String destination) throws InvalidDataException{
        if(!destination.matches("^[A-Z]{3}")||destination.equalsIgnoreCase(origin)) 
        {throw new InvalidDataException("Invalid destination! destination must consist of 3 UpperCase letters and not equal the destination0");}
        this.destination = destination;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
  
        this.departureTime = departureTime;
    }

    public void setDurationInMinutes(int durationInMinutes) throws InvalidDataException{
    if(durationInMinutes<30||durationInMinutes>1200) 
    {throw new InvalidDataException("Invalid duration! duration must be between 30 and 1200 minutes");}
        this.durationInMinutes = durationInMinutes;
    }
    

    public void setBaseFare(double baseFare) throws InvalidDataException{
        if(baseFare<=0||baseFare>20000.0)
        {throw new InvalidDataException("Invalid Base fare! base fare must be between 1 and 20,000 SAR");}
        this.baseFare = baseFare;
    }

    public void setFirstClassSeatCount(int firstClassSeatCount) throws InvalidDataException {
       if(firstClassSeatCount<0||firstClassSeatCount>20)
       {throw new InvalidDataException("Invalid amount of first class seats! the amount of first class seats must be between 0 and 20");}
        this.firstClassSeatCount = firstClassSeatCount;
    }

    public void setBusinessSeatCount(int businessSeatCount) throws InvalidDataException{
        if(businessSeatCount<0||businessSeatCount>60)
        {throw new InvalidDataException("Invalid amount of business seats! the quantity of business seats must be 0-60 seats");}    
        this.businessSeatCount = businessSeatCount;
    }

    public void setEconomySeatCount(int economySeatCount) throws InvalidDataException{
if(economySeatCount<1||economySeatCount>300) 
{throw new InvalidDataException("Invalid amount of seats! the number of seats must be 1-300");}
        this.economySeatCount = economySeatCount;
    }

    public void setSeats(ArrayList<Seat> seats) {
        this.seats = seats;
    }
private int buildCabinSeats(int startRow, int count,String cabinType ){
if(count<=0) {return startRow;}
int currentRow= startRow;
char[] letters= {'A','B','C','D'};
    for (int i = 0; i < count; i++) {
        if(i>0&&i%4==0){currentRow++;}
        String seatLetter= Character.toString(letters[i%4]);
       Seat seat;
        switch (cabinType) {
            case "First":
               seat = new FirstClassSeat(currentRow, false, seatLetter); break;
                case "Business":
                seat = new BusinessSeat(currentRow, false, seatLetter);
                break;
            default:
                seat = new EconomySeat(currentRow, false, seatLetter);
        }
        seats.add(seat);
    }
    return currentRow+1;
}
 private void generateSeats(){
 seats = new ArrayList<>();
 int currantRow=1;
 currantRow = buildCabinSeats(currantRow, firstClassSeatCount, "First");
 currantRow = buildCabinSeats(currantRow, businessSeatCount, "Business");
 currantRow = buildCabinSeats(currantRow, economySeatCount, "Economy");
 }   
    
  public void printSeatMap() {
    System.out.println("\n=================================");
    System.out.println(" Seat Map for Flight " + flightNumber + " (" + origin + " -> " + destination + ")");
    System.out.println(" Departure: " + departureTime);
    System.out.println("=================================");
    if (seats.isEmpty()) {
        System.out.println("No seats configured for this flight.");
        return;
    }

    Class<?> currentCabinClass = null;
    for (int i = 0; i < seats.size(); i++) {
        Seat s = seats.get(i);
        if (currentCabinClass != s.getClass()) {
            currentCabinClass = s.getClass();
            System.out.println("\n--- " + currentCabinClass.getSimpleName().replace("Seat", "").toUpperCase() + " CLASS ---");
        }
        if (s.getIsBooked()) {
            System.out.print("[ X ] ");
        } else {
            System.out.print("[" + s.getRow() + s.getLetter() + "] ");
        }       
        if ("B".equalsIgnoreCase(s.getLetter())) {
            System.out.print("| ");
        }

        boolean isLastInList = (i == seats.size() - 1);
        if ("D".equalsIgnoreCase(s.getLetter()) || isLastInList || (!isLastInList && seats.get(i + 1).getRow() != s.getRow())) {
            System.out.println();
        }
    }
    System.out.println("=================================");
}
public Seat getSeat(String seatNumber)  {
    for (Seat seat : seats) {
        if(seat.getSeatNumber().equals(seatNumber)) {return seat;}
    }
    return null;
}

    @Override
    public String getId() {
        return flightNumber;
    }

    @Override
    public String toFileLine() {
        return flightNumber+","+origin+","+destination+","+departureTime+","+durationInMinutes+","
         +baseFare+","+firstClassSeatCount+","+businessSeatCount+","+economySeatCount;
    }

    @Override
    public int compareTo(Flight f) {
       return departureTime.compareTo(f.departureTime);
    }
    @Override
    public boolean equals(Object o){
    if(this==o){return true;}
    if(o==null||getClass()!=o.getClass()){return false;}
    Flight f= (Flight) o;
    return Objects.equals(this.getFlightNumber(), f.getFlightNumber());
    }
    @Override
    public int hashCode(){
    return Objects.hash(getFlightNumber());
    }
}
