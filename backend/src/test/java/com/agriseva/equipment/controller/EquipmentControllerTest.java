package com.agriseva.equipment.controller;

import com.agriseva.common.exception.GlobalExceptionHandler;
import com.agriseva.equipment.dto.EquipmentResponse;
import com.agriseva.equipment.exception.EquipmentNotFoundException;
import com.agriseva.equipment.model.EquipmentCategory;
import com.agriseva.equipment.model.EquipmentStatus;
import com.agriseva.equipment.service.EquipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EquipmentControllerTest {

    @Mock
    private EquipmentService equipmentService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        EquipmentController controller =
                new EquipmentController(equipmentService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createShouldReturnCreatedEquipment() throws Exception {
        EquipmentResponse response = createResponse();

        when(equipmentService.create(
                eq("sharath.test@example.com"),
                any()
        )).thenReturn(response);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "sharath.test@example.com",
                        null
                );

        mockMvc.perform(post("/api/equipment")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name")
                        .value("Mahindra Tractor"))
                .andExpect(jsonPath("$.category")
                        .value("TRACTOR"))
                .andExpect(jsonPath("$.status")
                        .value("AVAILABLE"));
    }

    @Test
    void searchWithoutFiltersShouldReturnEquipmentList()
            throws Exception {

        when(equipmentService.search(
                null,
                null,
                null,
                null,
                null
        )).thenReturn(List.of(createResponse()));

        mockMvc.perform(get("/api/equipment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name")
                        .value("Mahindra Tractor"));
    }

    @Test
    void getByIdShouldReturnNotFound()
            throws Exception {

        when(equipmentService.getById(99L))
                .thenThrow(new EquipmentNotFoundException(99L));

        mockMvc.perform(get("/api/equipment/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Equipment not found with ID: 99"));
    }

    @Test
    void createShouldRejectInvalidRequest()
            throws Exception {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "sharath.test@example.com",
                        null
                );

        mockMvc.perform(post("/api/equipment")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "category": null,
                                  "rentalPricePerDay": 0,
                                  "serviceAddress": "",
                                  "village": "",
                                  "district": "",
                                  "state": "",
                                  "postalCode": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"));
    }

    @Test
    void searchShouldPassFiltersToService()
            throws Exception {
    
        when(equipmentService.search(
                EquipmentCategory.TRACTOR,
                "Siddipet",
                "Chinnagundavelly",
                EquipmentStatus.AVAILABLE,
                "Mahindra"
        )).thenReturn(List.of(createResponse()));
    
        mockMvc.perform(get("/api/equipment")
                        .param("category", "TRACTOR")
                        .param("district", "Siddipet")
                        .param("village", "Chinnagundavelly")
                        .param("status", "AVAILABLE")
                        .param("keyword", "Mahindra"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name")
                        .value("Mahindra Tractor"));
    }    

    private EquipmentResponse createResponse() {
        return EquipmentResponse.builder()
                .id(10L)
                .ownerId(1L)
                .ownerName("Sharath Reddy")
                .name("Mahindra Tractor")
                .category(EquipmentCategory.TRACTOR)
                .description("55 HP tractor")
                .rentalPricePerDay(
                        new BigDecimal("2500.00")
                )
                .securityDeposit(
                        new BigDecimal("5000.00")
                )
                .status(EquipmentStatus.AVAILABLE)
                .serviceAddress("Main Road")
                .village("Chinnagundavelly")
                .district("Siddipet")
                .state("Telangana")
                .postalCode("502103")
                .imageUrl("https://example.com/tractor.jpg")
                .active(true)
                .build();
    }

    private String validRequestJson() {
        return """
                {
                  "name": "Mahindra Tractor",
                  "category": "TRACTOR",
                  "description": "55 HP tractor",
                  "rentalPricePerDay": 2500.00,
                  "securityDeposit": 5000.00,
                  "status": "AVAILABLE",
                  "serviceAddress": "Main Road",
                  "village": "Chinnagundavelly",
                  "district": "Siddipet",
                  "state": "Telangana",
                  "postalCode": "502103",
                  "imageUrl": "https://example.com/tractor.jpg"
                }
                """;
    }
}