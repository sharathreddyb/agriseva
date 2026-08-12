package com.agriseva.user.service;

import com.agriseva.user.dto.provider.ProviderProfileRequest;
import com.agriseva.user.dto.provider.ProviderProfileResponse;

public interface ProviderProfileService {

    ProviderProfileResponse createOrUpdate(
            String authenticatedEmail,
            ProviderProfileRequest request
    );
}