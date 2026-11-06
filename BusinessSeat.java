/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cs102.project.step1;

/**
 *
 * @author saleh
 */
public class BusinessSeat extends Seat{

    public BusinessSeat(int row, boolean isBooked, String letter) {
        super(row, isBooked, letter);
    }

    

    @Override
    public int getBaggageAllowanceKg() {
      return 32;
    }

    @Override
    public String getPerks() {
       return "Gourmet meal, Business center at the lobby, 2 checked bags, and free wifi";
    }
     public String toString(){
    return "Seat "+getSeatNumber()+" ("+getClass().getSimpleName()+"): "+getPerks()+", Baggage: "+getBaggageAllowanceKg()+" KG";
}

    @Override
    public double getPriceMultiplier() {
       return 2.5;
    }
}