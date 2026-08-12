package com.agriseva.rental.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RentalRequest {

    @NotNull(message = "Equipment ID is required")
    private Long equipmentId;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or a future date")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must be today or a future date")
    private LocalDate endDate;

    @Size(
            max = 500,
            message = "Farmer note must not exceed 500 characters"
    )
    private String farmerNote;
}