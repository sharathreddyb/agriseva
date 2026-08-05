package com.agriseva.user.controller;

import com.agriseva.user.dto.provider.ProviderProfileResponse;
import com.agriseva.user.model.ProviderType;
import com.agriseva.user.model.RoleType;
import com.agriseva.user.model.VerificationStatus;
import com.agriseva.user.service.ProviderProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProviderProfileControllerTest {

    @Mock
    private ProviderProfileService providerProfileService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ProviderProfileController controller =
                new ProviderProfileController(
                        providerProfileService
                );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void createOrUpdateShouldReturnProviderProfile()
            throws Exception {

        ProviderProfileResponse response =
                ProviderProfileResponse.builder()
                        .id(10L)
                        .userId(1L)
                        .businessName("Sharath Agro Services")
                        .description("Farm equipment and products")
                        .providerType(ProviderType.BOTH)
                        .verificationStatus(
                                VerificationStatus.UNVERIFIED
                        )
                        .serviceAddress("Main Road")
                        .village("Chinnagundavelly")
                        .district("Siddipet")
                        .state("Telangana")
                        .postalCode("502103")
                        .roles(Set.of(
                                RoleType.FARMER,
                                RoleType.EQUIPMENT_OWNER,
                                RoleType.PRODUCT_SELLER
                        ))
                        .build();

        when(providerProfileService.createOrUpdate(
                eq("sharath.test@example.com"),
                any()
        )).thenReturn(response);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "sharath.test@example.com",
                        null
                );

        mockMvc.perform(post(
                        "/api/users/me/provider-profile"
                )
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "businessName": "Sharath Agro Services",
                                  "description": "Farm equipment and products",
                                  "providerType": "BOTH",
                                  "serviceAddress": "Main Road",
                                  "village": "Chinnagundavelly",
                                  "district": "Siddipet",
                                  "state": "Telangana",
                                  "postalCode": "502103"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.providerType").value("BOTH"))
                .andExpect(jsonPath("$.verificationStatus")
                        .value("UNVERIFIED"))
                .andExpect(jsonPath("$.roles").isArray());
    }

    @Test
    void createOrUpdateShouldRejectInvalidRequest()
            throws Exception {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "sharath.test@example.com",
                        null
                );

        mockMvc.perform(post(
                        "/api/users/me/provider-profile"
                )
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "businessName": "Sharath Agro Services",
                                  "providerType": null,
                                  "serviceAddress": "",
                                  "village": "",
                                  "district": "",
                                  "state": "",
                                  "postalCode": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}