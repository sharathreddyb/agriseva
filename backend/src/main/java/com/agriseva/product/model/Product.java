package com.agriseva.product.model;

import com.agriseva.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "seller_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_products_seller"
            )
    )
    private User seller;

    @Column(
            nullable = false,
            length = 150
    )
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private ProductCategory category;

    @Column(length = 1000)
    private String description;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal price;

    @Column(
            name = "stock_quantity",
            nullable = false
    )
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private ProductUnit unit;

    @Column(
            name = "image_url",
            length = 500
    )
    private String imageUrl;

    @Column(
            name = "service_address",
            nullable = false,
            length = 255
    )
    private String serviceAddress;

    @Column(
            nullable = false,
            length = 100
    )
    private String village;

    @Column(
            nullable = false,
            length = 100
    )
    private String district;

    @Column(
            nullable = false,
            length = 100
    )
    private String state;

    @Column(
            name = "postal_code",
            nullable = false,
            length = 10
    )
    private String postalCode;

    @Column(nullable = false)
    private boolean active = true;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    public void beforeCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void beforeUpdate() {
        updatedAt = LocalDateTime.now();
    }
}