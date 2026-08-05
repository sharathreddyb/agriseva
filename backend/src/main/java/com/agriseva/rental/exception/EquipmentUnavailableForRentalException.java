package com.agriseva.rental.exception;

public class EquipmentUnavailableForRentalException
        extends RuntimeException {

    public EquipmentUnavailableForRentalException() {
        super("Equipment is not available for rental");
    }
}