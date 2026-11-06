/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.mycompany.cs102.project.step1;

/**
 *
 * @author saleh
 */
public enum Cabin {
     FIRST_cLASS(4.0,40,false),
   BUSINESS_CLASS(2.5,32,false),
   ECONOMY_CLASS(1.0,23,true);
    private final double priceMultiplyer; 
    private final int baggageAllowance; 
    private final boolean upgradeEligability;

    private Cabin(double priceMultiplyer, int baggageAllowance, boolean upgradeEligability) {
        this.priceMultiplyer = priceMultiplyer;
        this.baggageAllowance = baggageAllowance;
        this.upgradeEligability = upgradeEligability;
    }

    public int getBaggageAllowance() {
        return baggageAllowance;
    }

    public double getPriceMultiplyer() {
        return priceMultiplyer;
    }
    public boolean getUpgradeEligability(){return upgradeEligability;}
  
}
