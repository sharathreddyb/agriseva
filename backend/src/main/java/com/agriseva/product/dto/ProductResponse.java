package com.agriseva.product.dto;

import com.agriseva.product.model.ProductCategory;
import com.agriseva.product.model.ProductUnit;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProductResponse {

    private Long id;

    private Long sellerId;

    private String sellerName;

    private String name;

    private ProductCategory category;

    private String description;

    private BigDecimal price;

    private Integer stockQuantity;

    private ProductUnit unit;

    private String imageUrl;

    private String serviceAddress;

    private String village;

    private String district;

    private String state;

    private String postalCode;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}