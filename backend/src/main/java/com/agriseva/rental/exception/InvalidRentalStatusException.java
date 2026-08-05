package com.agriseva.rental.exception;

public class InvalidRentalStatusException extends RuntimeException {

    public InvalidRentalStatusException(String message) {
        super(message);
    }
}