package com.agriseva.user.controller;

import com.agriseva.user.dto.provider.ProviderProfileRequest;
import com.agriseva.user.dto.provider.ProviderProfileResponse;
import com.agriseva.user.service.ProviderProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me/provider-profile")
@RequiredArgsConstructor
public class ProviderProfileController {

    private final ProviderProfileService providerProfileService;

    @PostMapping
    public ResponseEntity<ProviderProfileResponse> createOrUpdate(
            Authentication authentication,
            @Valid @RequestBody ProviderProfileRequest request
    ) {
        ProviderProfileResponse response =
                providerProfileService.createOrUpdate(
                        authentication.getName(),
                        request
                );

        return ResponseEntity.ok(response);
    }
}