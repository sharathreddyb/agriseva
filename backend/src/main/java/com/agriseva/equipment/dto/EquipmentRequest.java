package com.agriseva.equipment.dto;

import com.agriseva.equipment.model.EquipmentCategory;
import com.agriseva.equipment.model.EquipmentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EquipmentRequest {

    @NotBlank(message = "Equipment name is required")
    @Size(max = 150, message = "Equipment name must not exceed 150 characters")
    private String name;

    @NotNull(message = "Equipment category is required")
    private EquipmentCategory category;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Rental price per day is required")
    @DecimalMin(
            value = "0.01",
            message = "Rental price per day must be greater than zero"
    )
    private BigDecimal rentalPricePerDay;

    @DecimalMin(
            value = "0.00",
            message = "Security deposit must not be negative"
    )
    private BigDecimal securityDeposit;

    private EquipmentStatus status;

    @NotBlank(message = "Service address is required")
    @Size(max = 255, message = "Service address must not exceed 255 characters")
    private String serviceAddress;

    @NotBlank(message = "Village is required")
    @Size(max = 100, message = "Village must not exceed 100 characters")
    private String village;

    @NotBlank(message = "District is required")
    @Size(max = 100, message = "District must not exceed 100 characters")
    private String district;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @NotBlank(message = "Postal code is required")
    @Size(max = 10, message = "Postal code must not exceed 10 characters")
    private String postalCode;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;
}