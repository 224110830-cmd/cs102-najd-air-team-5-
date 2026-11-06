/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mycompany.cs102.project.step1;

/**
 *
 * @author saleh
 */
public enum FrequentFlyerTier {
     PLATINUM(1,0.15),
     GOLD(2, 0.10),
     SILVER(3, 0.05),
     NONE(4, 0.0);
    private final double discount;
 private  int priority;
    private FrequentFlyerTier(int priority, double discount) {
        this.discount = discount;
        this.priority=priority;
    }

    public double getDiscount() {
        return discount;
    }

    public int getPriority() {
        return priority;
    }
    
}
