package com.mycompany.cs102.project.step1;

/**
 * Thrown when a business rule is broken, for example:
 * "seat already taken" or "passenger already has a seat on this flight".
 *
 * It extends Exception (not RuntimeException), so it is a CHECKED exception:
 * the compiler forces every caller to either catch it or declare "throws".
 * That is on purpose: a broken business rule is something the program
 * should expect and handle, not a bug.
 *
 * (InvalidDataException is the other checked exception in the project:
 * it is for bad VALUES such as a bad email. This one is for bad ACTIONS.)
 *
 * @author Member 2
 */
public class BookingException extends Exception {

    public BookingException(String message) {
        super(message);
    }
}
