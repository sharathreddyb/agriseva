package com.agriseva.rental.exception;

public class InvalidRentalDatesException extends RuntimeException {

    public InvalidRentalDatesException() {
        super("Rental end date must be on or after the start date");
    }
}