package com.agriseva.rental.dto;

import com.agriseva.rental.model.RentalStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class RentalResponse {

    private Long id;

    private Long equipmentId;

    private String equipmentName;

    private Long farmerId;

    private String farmerName;

    private Long ownerId;

    private String ownerName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer totalDays;

    private BigDecimal rentalPricePerDay;

    private BigDecimal totalAmount;

    private RentalStatus status;

    private String farmerNote;

    private String ownerResponseNote;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}