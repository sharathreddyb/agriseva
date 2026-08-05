package com.agriseva.product.dto;

import com.agriseva.product.model.ProductCategory;
import com.agriseva.product.model.ProductUnit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(
            max = 150,
            message = "Product name must not exceed 150 characters"
    )
    private String name;

    @NotNull(message = "Product category is required")
    private ProductCategory category;

    @Size(
            max = 1000,
            message = "Description must not exceed 1000 characters"
    )
    private String description;

    @NotNull(message = "Product price is required")
    @DecimalMin(
            value = "0.01",
            message = "Product price must be greater than zero"
    )
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(
            value = 0,
            message = "Stock quantity cannot be negative"
    )
    private Integer stockQuantity;

    @NotNull(message = "Product unit is required")
    private ProductUnit unit;

    @Size(
            max = 500,
            message = "Image URL must not exceed 500 characters"
    )
    private String imageUrl;

    @NotBlank(message = "Service address is required")
    @Size(
            max = 255,
            message = "Service address must not exceed 255 characters"
    )
    private String serviceAddress;

    @NotBlank(message = "Village is required")
    @Size(
            max = 100,
            message = "Village must not exceed 100 characters"
    )
    private String village;

    @NotBlank(message = "District is required")
    @Size(
            max = 100,
            message = "District must not exceed 100 characters"
    )
    private String district;

    @NotBlank(message = "State is required")
    @Size(
            max = 100,
            message = "State must not exceed 100 characters"
    )
    private String state;

    @NotBlank(message = "Postal code is required")
    @Pattern(
            regexp = "^[0-9]{6}$",
            message = "Postal code must contain exactly 6 digits"
    )
    private String postalCode;
}