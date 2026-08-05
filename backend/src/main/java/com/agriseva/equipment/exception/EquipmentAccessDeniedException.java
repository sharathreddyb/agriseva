package com.agriseva.equipment.exception;

public class EquipmentAccessDeniedException extends RuntimeException {

    public EquipmentAccessDeniedException() {
        super("You are not allowed to modify this equipment listing");
    }
}