package com.agriseva.equipment.dto;

import com.agriseva.equipment.model.EquipmentCategory;
import com.agriseva.equipment.model.EquipmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class EquipmentResponse {

    private Long id;

    private Long ownerId;

    private String ownerName;

    private String name;

    private EquipmentCategory category;

    private String description;

    private BigDecimal rentalPricePerDay;

    private BigDecimal securityDeposit;

    private EquipmentStatus status;

    private String serviceAddress;

    private String village;

    private String district;

    private String state;

    private String postalCode;

    private String imageUrl;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}