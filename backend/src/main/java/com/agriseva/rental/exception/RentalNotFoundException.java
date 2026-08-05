package com.agriseva.rental.exception;

public class RentalNotFoundException extends RuntimeException {

    public RentalNotFoundException(Long rentalId) {
        super("Rental request not found with ID: " + rentalId);
    }
}