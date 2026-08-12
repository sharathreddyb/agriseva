package com.agriseva.equipment.exception;

public class EquipmentOwnerRoleRequiredException extends RuntimeException {

    public EquipmentOwnerRoleRequiredException() {
        super("Equipment owner role is required to manage equipment listings");
    }
}