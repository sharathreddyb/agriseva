package com.agriseva.equipment.exception;

public class EquipmentNotFoundException extends RuntimeException {

    public EquipmentNotFoundException(Long equipmentId) {
        super("Equipment not found with ID: " + equipmentId);
    }
}