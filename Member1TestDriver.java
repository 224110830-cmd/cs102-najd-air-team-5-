/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cs102.project.step1;

import java.time.LocalDateTime;

/**
 *
 * @author saleh
 */
public class Member1TestDriver {
 
    public static void main(String[] args) {
        System.out.println("=== Step 1: Testing passenger instantiation and invalid data handeling===");
       try {
          Passenger p1= new Passenger("P0001", "Saleh Alomair", "Saleh123@gmail.com", "0558104888", FrequentFlyerTier.PLATINUM);
           System.out.println("Created Passenger: "+p1.toString());
        } catch (InvalidDataException e) {
           System.out.println("caught exception: "+e.getMessage());
        }
        
        System.out.println("testing invalid Data (specifically, a bad ID): ");
        try {
            Passenger p2= new Passenger("P-0002!", "Fahad turki", "Fahad123@hotMail.com", "0558104888", FrequentFlyerTier.SILVER);
        } catch (InvalidDataException e) {
            System.out.println("caught exception: "+e.getMessage());   
}
        
        System.out.println("=== Step 2: Testing Flight seat grid generation ===");
        
        try {
            Flight f1= new Flight("NA001", "RUH", "MAD", LocalDateTime.now(), 90, 500.0, 2, 4, 8);
            System.out.println("Flight: "+f1);
            f1.printSeatMap();
            System.out.println("=== Step 3: testing seat booking and pricing ===");
            Seat s= f1.getSeat("1A");
            if(s!=null) {
            Passenger p1 = new Passenger("P0001", "Saleh Alomair", "Saleh123@gmail.com", "0558104888", FrequentFlyerTier.PLATINUM);
            double price =  s.calculatePrice(f1.getBaseFare(), p1.getTier());
                System.out.println("Seat 1A Price for PLATINUM passenger: "+price+ "SAR");
                s.book();
                System.out.println("Booked Seat 1A.");
             System.out.println("=== Step 4: Testing storeable output ===");
                System.out.println("FLight CSV Line: "+f1.toFileLine());
            }
        } catch (InvalidDataException e) {
            System.out.println("caught Exception: "+e.getMessage());
        }
        
       
    }

}
