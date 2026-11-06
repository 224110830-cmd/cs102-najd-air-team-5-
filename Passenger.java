/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cs102.project.step1;
import java.util.*;


/**
 *
 * @author saleh
 */
public class Passenger implements Storeable{
    private  String passengerId;
    private  String fullName;
    private  String email;
    private String phone;
    private FrequentFlyerTier tier;

    

    public Passenger(String passengerId, String fullName, String email, String phone, FrequentFlyerTier tier) throws InvalidDataException{
        setEmail(email);
        setFullName(fullName);
        setPassengerId(passengerId);
        setPhone(phone);
        setTier(tier);
    }
    

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

   

    public String getPhone() {
        return phone;
    }

    public FrequentFlyerTier getTier() {
        return tier;
    }
    public void setPassengerId(String passengerId) throws InvalidDataException {
        if (passengerId == null || !passengerId.matches("^P\\d{4}$")) {
            throw new InvalidDataException("Invalid Passenger ID format: Must be P followed by 4 digits (e.g., P1001).");
        }
        this.passengerId = passengerId;
    }
    public void setFullName(String fullName)throws InvalidDataException {
        if (fullName == null || !fullName.matches("^[a-zA-Z' -]{2,50}$")) {
            throw new InvalidDataException("Invalid Name: Must be 2-50 characters.");
        }
        this.fullName = fullName;
    }

    public void setEmail(String email) throws InvalidDataException {
        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new InvalidDataException("Invalid Email format.");
        }
        this.email = email;
    } 

    public void setPhone(String phone) throws InvalidDataException{
        if (phone == null || !phone.matches("^05\\d{8}$")) {
          
            throw new InvalidDataException("Invalid phone number: Must be 10 digits starting with 05.");
        }
        this.phone = phone;
    }

    public void setTier(FrequentFlyerTier tier) throws InvalidDataException {
        if (tier == null) {
            throw new InvalidDataException("Tier cannot be null.");
        }
        this.tier = tier;
    }

    @Override
    public String getId() {
       return passengerId;
    }

    @Override
    public String toFileLine() {
      return  getId() + "," + getFullName() + "," + getEmail() + "," + getPhone() + "," + getTier();
    }
    @Override
    public String toString(){
    return "ID: "+getId()+" | Name: "+getFullName()+" | Email:"+getEmail()+" | Phone: "+getPhone()+" | Teir: "+getTier();
    }
    @Override
    public boolean equals(Object o){
    if(this==o){return true;}
    if(o==null||getClass()!=o.getClass()) {return false;}
    Passenger p = (Passenger) o;
    return Objects.equals(this.getId(), p.getId());
    }
    @Override
    public int hashCode(){
    return Objects.hash(getId());
    }
    
}
