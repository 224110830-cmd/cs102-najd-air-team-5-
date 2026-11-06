/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cs102.project.step1;

public abstract class Seat {
    private int row; 
    private boolean isBooked;
    private String letter;

    public Seat(int row, boolean isBooked, String letter) {
        this.row = row;
        setIsBooked(isBooked);
        this.letter = letter;
    }

    // --- GETTERS & SETTERS ---
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public boolean getIsBooked() {
        return isBooked;
    }

    public void setIsBooked(boolean isBooked) {
        this.isBooked = isBooked;
    }

    // --- DOMAIN METHODS ---
    public void book() {
        this.isBooked = true;
    }

    public void release() {
        this.isBooked = false;
    }

    public String getSeatNumber() {
        return getRow() + getLetter(); // Fixed: Produces "1A" instead of "A1"
    }

    // --- ABSTRACT METHODS ---
    public abstract double getPriceMultiplier(); // Fixed spelling
    public abstract int getBaggageAllowanceKg();
    public abstract String getPerks();

    // --- POLYMORPHIC PRICING ---
    public double calculatePrice(double baseFare, FrequentFlyerTier tier) {
        double discount = (tier != null) ? tier.getDiscount() : 0.0;
        double price = baseFare * getPriceMultiplier() * (1.0 - discount);
        return Math.round(price * 100.0) / 100.0; // Rounds safely to 2 decimal places
    }
}