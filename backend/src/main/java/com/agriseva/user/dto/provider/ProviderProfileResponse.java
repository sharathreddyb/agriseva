package com.agriseva.user.dto.provider;

import com.agriseva.user.model.ProviderType;
import com.agriseva.user.model.RoleType;
import com.agriseva.user.model.VerificationStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class ProviderProfileResponse {

    private Long id;

    private Long userId;

    private String businessName;

    private String description;

    private ProviderType providerType;

    private VerificationStatus verificationStatus;

    private String serviceAddress;

    private String village;

    private String district;

    private String state;

    private String postalCode;

    private Set<RoleType> roles;
}