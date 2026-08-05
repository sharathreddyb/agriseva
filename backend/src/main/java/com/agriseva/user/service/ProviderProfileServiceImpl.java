package com.agriseva.user.service;

import com.agriseva.user.dto.provider.ProviderProfileRequest;
import com.agriseva.user.dto.provider.ProviderProfileResponse;
import com.agriseva.user.exception.RoleNotFoundException;
import com.agriseva.user.model.ProviderProfile;
import com.agriseva.user.model.ProviderType;
import com.agriseva.user.model.Role;
import com.agriseva.user.model.RoleType;
import com.agriseva.user.model.User;
import com.agriseva.user.model.VerificationStatus;
import com.agriseva.user.repository.ProviderProfileRepository;
import com.agriseva.user.repository.RoleRepository;
import com.agriseva.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderProfileServiceImpl
        implements ProviderProfileService {

    private final UserRepository userRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public ProviderProfileResponse createOrUpdate(
            String authenticatedEmail,
            ProviderProfileRequest request
    ) {
        User user = userRepository
                .findByEmailIgnoreCase(authenticatedEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user was not found"
                        )
                );

        ProviderProfile providerProfile = providerProfileRepository
                .findByUserId(user.getId())
                .orElseGet(() -> createNewProfile(user));

        updateProfile(providerProfile, request);
        updateProviderRoles(user, request.getProviderType());

        ProviderProfile savedProfile =
                providerProfileRepository.save(providerProfile);

        userRepository.save(user);

        return buildResponse(savedProfile, user);
    }

    private ProviderProfile createNewProfile(User user) {
        ProviderProfile providerProfile = new ProviderProfile();
        providerProfile.setUser(user);
        providerProfile.setVerificationStatus(
                VerificationStatus.UNVERIFIED
        );

        return providerProfile;
    }

    private void updateProfile(
            ProviderProfile providerProfile,
            ProviderProfileRequest request
    ) {
        providerProfile.setBusinessName(
                trimToNull(request.getBusinessName())
        );
        providerProfile.setDescription(
                trimToNull(request.getDescription())
        );
        providerProfile.setProviderType(request.getProviderType());
        providerProfile.setServiceAddress(
                request.getServiceAddress().trim()
        );
        providerProfile.setVillage(request.getVillage().trim());
        providerProfile.setDistrict(request.getDistrict().trim());
        providerProfile.setState(request.getState().trim());
        providerProfile.setPostalCode(request.getPostalCode().trim());
    }

    private void updateProviderRoles(
            User user,
            ProviderType providerType
    ) {
        user.getRoles().removeIf(role ->
                role.getName() == RoleType.EQUIPMENT_OWNER
                        || role.getName() == RoleType.PRODUCT_SELLER
        );

        if (providerType == ProviderType.EQUIPMENT_OWNER
                || providerType == ProviderType.BOTH) {
            user.addRole(getRole(RoleType.EQUIPMENT_OWNER));
        }

        if (providerType == ProviderType.PRODUCT_SELLER
                || providerType == ProviderType.BOTH) {
            user.addRole(getRole(RoleType.PRODUCT_SELLER));
        }
    }

    private Role getRole(RoleType roleType) {
        return roleRepository
                .findByName(roleType)
                .orElseThrow(() ->
                        new RoleNotFoundException(roleType)
                );
    }

    private ProviderProfileResponse buildResponse(
            ProviderProfile providerProfile,
            User user
    ) {
        Set<RoleType> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return ProviderProfileResponse.builder()
                .id(providerProfile.getId())
                .userId(user.getId())
                .businessName(providerProfile.getBusinessName())
                .description(providerProfile.getDescription())
                .providerType(providerProfile.getProviderType())
                .verificationStatus(
                        providerProfile.getVerificationStatus()
                )
                .serviceAddress(providerProfile.getServiceAddress())
                .village(providerProfile.getVillage())
                .district(providerProfile.getDistrict())
                .state(providerProfile.getState())
                .postalCode(providerProfile.getPostalCode())
                .roles(roles)
                .build();
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}