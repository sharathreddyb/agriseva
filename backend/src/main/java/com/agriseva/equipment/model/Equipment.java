package com.agriseva.equipment.model;

import com.agriseva.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipment")
@Getter
@Setter
@NoArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "owner_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_equipment_owner")
    )
    private User owner;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 40)
    private EquipmentCategory category;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(
            name = "rental_price_per_day",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal rentalPricePerDay;

    @Column(
            name = "security_deposit",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal securityDeposit = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private EquipmentStatus status = EquipmentStatus.AVAILABLE;

    @Column(name = "service_address", nullable = false, length = 255)
    private String serviceAddress;

    @Column(name = "village", nullable = false, length = 100)
    private String village;

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void beforeCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = EquipmentStatus.AVAILABLE;
        }

        if (securityDeposit == null) {
            securityDeposit = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void beforeUpdate() {
        updatedAt = LocalDateTime.now();
    }
}