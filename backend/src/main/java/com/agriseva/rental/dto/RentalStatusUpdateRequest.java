package com.agriseva.rental.dto;

import com.agriseva.rental.model.RentalStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalStatusUpdateRequest {

    @NotNull(message = "Rental status is required")
    private RentalStatus status;

    @Size(
            max = 500,
            message = "Owner response note must not exceed 500 characters"
    )
    private String ownerResponseNote;
}