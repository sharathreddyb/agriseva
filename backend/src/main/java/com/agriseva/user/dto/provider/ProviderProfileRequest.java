package com.agriseva.user.dto.provider;

import com.agriseva.user.model.ProviderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderProfileRequest {

    @Size(max = 150, message = "Business name must not exceed 150 characters")
    private String businessName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Provider type is required")
    private ProviderType providerType;

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
}