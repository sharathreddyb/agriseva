package com.agriseva.user.service;

import com.agriseva.user.dto.provider.ProviderProfileRequest;
import com.agriseva.user.dto.provider.ProviderProfileResponse;
import com.agriseva.user.model.ProviderProfile;
import com.agriseva.user.model.ProviderType;
import com.agriseva.user.model.Role;
import com.agriseva.user.model.RoleType;
import com.agriseva.user.model.User;
import com.agriseva.user.model.VerificationStatus;
import com.agriseva.user.repository.ProviderProfileRepository;
import com.agriseva.user.repository.RoleRepository;
import com.agriseva.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProviderProfileServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProviderProfileRepository providerProfileRepository;

    @Mock
    private RoleRepository roleRepository;

    private ProviderProfileServiceImpl providerProfileService;

    @BeforeEach
    void setUp() {
        providerProfileService = new ProviderProfileServiceImpl(
                userRepository,
                providerProfileRepository,
                roleRepository
        );
    }

    @Test
    void createOrUpdateShouldCreateEquipmentOwnerProfile() {
        User user = createUser();

        Role equipmentOwnerRole =
                new Role(RoleType.EQUIPMENT_OWNER);

        ProviderProfileRequest request =
                createRequest(ProviderType.EQUIPMENT_OWNER);

        when(userRepository.findByEmailIgnoreCase(
                "sharath.test@example.com"
        )).thenReturn(Optional.of(user));

        when(providerProfileRepository.findByUserId(1L))
                .thenReturn(Optional.empty());

        when(roleRepository.findByName(
                RoleType.EQUIPMENT_OWNER
        )).thenReturn(Optional.of(equipmentOwnerRole));

        when(providerProfileRepository.save(any()))
                .thenAnswer(invocation -> {
                    ProviderProfile profile = invocation.getArgument(0);
                    profile.setId(10L);
                    return profile;
                });

        ProviderProfileResponse response =
                providerProfileService.createOrUpdate(
                        "sharath.test@example.com",
                        request
                );

        assertEquals(10L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals(
                ProviderType.EQUIPMENT_OWNER,
                response.getProviderType()
        );
        assertEquals(
                VerificationStatus.UNVERIFIED,
                response.getVerificationStatus()
        );
        assertTrue(response.getRoles().contains(RoleType.FARMER));
        assertTrue(
                response.getRoles().contains(
                        RoleType.EQUIPMENT_OWNER
                )
        );
        assertFalse(
                response.getRoles().contains(
                        RoleType.PRODUCT_SELLER
                )
        );

        verify(providerProfileRepository).save(any());
        verify(userRepository).save(user);
    }

    @Test
    void createOrUpdateShouldAddBothProviderRoles() {
        User user = createUser();

        Role equipmentOwnerRole =
                new Role(RoleType.EQUIPMENT_OWNER);
        Role productSellerRole =
                new Role(RoleType.PRODUCT_SELLER);

        ProviderProfileRequest request =
                createRequest(ProviderType.BOTH);

        when(userRepository.findByEmailIgnoreCase(
                "sharath.test@example.com"
        )).thenReturn(Optional.of(user));

        when(providerProfileRepository.findByUserId(1L))
                .thenReturn(Optional.empty());

        when(roleRepository.findByName(
                RoleType.EQUIPMENT_OWNER
        )).thenReturn(Optional.of(equipmentOwnerRole));

        when(roleRepository.findByName(
                RoleType.PRODUCT_SELLER
        )).thenReturn(Optional.of(productSellerRole));

        when(providerProfileRepository.save(any()))
                .thenAnswer(invocation -> {
                    ProviderProfile profile = invocation.getArgument(0);
                    profile.setId(10L);
                    return profile;
                });

        ProviderProfileResponse response =
                providerProfileService.createOrUpdate(
                        "sharath.test@example.com",
                        request
                );

        assertTrue(response.getRoles().contains(RoleType.FARMER));
        assertTrue(
                response.getRoles().contains(
                        RoleType.EQUIPMENT_OWNER
                )
        );
        assertTrue(
                response.getRoles().contains(
                        RoleType.PRODUCT_SELLER
                )
        );
    }

    @Test
    void createOrUpdateShouldUpdateExistingProfileAndRoles() {
        User user = createUser();
        user.addRole(new Role(RoleType.EQUIPMENT_OWNER));
        user.addRole(new Role(RoleType.PRODUCT_SELLER));

        ProviderProfile existingProfile = new ProviderProfile();
        existingProfile.setId(10L);
        existingProfile.setUser(user);
        existingProfile.setProviderType(ProviderType.BOTH);
        existingProfile.setVerificationStatus(
                VerificationStatus.UNVERIFIED
        );

        ProviderProfileRequest request =
                createRequest(ProviderType.PRODUCT_SELLER);

        Role productSellerRole =
                new Role(RoleType.PRODUCT_SELLER);

        when(userRepository.findByEmailIgnoreCase(
                "sharath.test@example.com"
        )).thenReturn(Optional.of(user));

        when(providerProfileRepository.findByUserId(1L))
                .thenReturn(Optional.of(existingProfile));

        when(roleRepository.findByName(
                RoleType.PRODUCT_SELLER
        )).thenReturn(Optional.of(productSellerRole));

        when(providerProfileRepository.save(existingProfile))
                .thenReturn(existingProfile);

        ProviderProfileResponse response =
                providerProfileService.createOrUpdate(
                        "sharath.test@example.com",
                        request
                );

        assertEquals(
                ProviderType.PRODUCT_SELLER,
                response.getProviderType()
        );
        assertTrue(response.getRoles().contains(RoleType.FARMER));
        assertTrue(
                response.getRoles().contains(
                        RoleType.PRODUCT_SELLER
                )
        );
        assertFalse(
                response.getRoles().contains(
                        RoleType.EQUIPMENT_OWNER
                )
        );
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setFullName("Sharath Reddy");
        user.setEmail("sharath.test@example.com");
        user.setPhoneNumber("9876543210");
        user.addRole(new Role(RoleType.FARMER));
        return user;
    }

    private ProviderProfileRequest createRequest(
            ProviderType providerType
    ) {
        ProviderProfileRequest request =
                new ProviderProfileRequest();

        request.setBusinessName("Sharath Agro Services");
        request.setDescription("Farm equipment and products");
        request.setProviderType(providerType);
        request.setServiceAddress("Main Road");
        request.setVillage("Chinnagundavelly");
        request.setDistrict("Siddipet");
        request.setState("Telangana");
        request.setPostalCode("502103");

        return request;
    }
}