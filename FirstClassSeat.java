/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cs102.project.step1;

/**
 *
 * @author saleh
 */
public class FirstClassSeat extends Seat {

    public FirstClassSeat(int row, boolean isBooked, String letter) {
        super(row, isBooked, letter);
    }

   

    @Override
    public int getBaggageAllowanceKg() {
        return 40;
    }

    @Override
    public String getPerks() {
       return "Private suite, fine dining, and 2 checked bags";
    }
    @Override
    public String toString(){
    return "Seat "+getSeatNumber()+" ("+getClass().getSimpleName()+"): "+getPerks()+", Baggage: "+getBaggageAllowanceKg()+" KG"; 
}

    @Override
    public double getPriceMultiplier() {
       return 4.0;
    }
}