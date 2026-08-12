package com.agriseva.rental.controller;

import com.agriseva.common.exception.GlobalExceptionHandler;
import com.agriseva.rental.dto.RentalResponse;
import com.agriseva.rental.exception.RentalNotFoundException;
import com.agriseva.rental.model.RentalStatus;
import com.agriseva.rental.service.EquipmentRentalService;
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
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EquipmentRentalControllerTest {

    @Mock
    private EquipmentRentalService rentalService;

    private MockMvc mockMvc;

    private UsernamePasswordAuthenticationToken authentication;

    @BeforeEach
    void setUp() {
        EquipmentRentalController controller =
                new EquipmentRentalController(rentalService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        authentication =
                new UsernamePasswordAuthenticationToken(
                        "farmer@example.com",
                        null
                );
    }

    @Test
    void createShouldReturnCreatedRental() throws Exception {
        when(rentalService.create(
                eq("farmer@example.com"),
                any()
        )).thenReturn(createResponse());

        mockMvc.perform(post("/api/rentals")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRentalRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.equipmentId").value(10))
                .andExpect(jsonPath("$.totalDays").value(3))
                .andExpect(jsonPath("$.status")
                        .value("PENDING"));
    }

    @Test
    void getMyRentalsShouldReturnList() throws Exception {
        when(rentalService.getMyRentals(
                "farmer@example.com"
        )).thenReturn(List.of(createResponse()));

        mockMvc.perform(get("/api/rentals/mine")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].equipmentName")
                        .value("Mahindra Tractor"));
    }

    @Test
    void updateStatusShouldReturnUpdatedRental()
            throws Exception {

        RentalResponse approvedResponse =
                RentalResponse.builder()
                        .id(100L)
                        .status(RentalStatus.APPROVED)
                        .build();

        when(rentalService.updateStatus(
                eq("farmer@example.com"),
                eq(100L),
                any()
        )).thenReturn(approvedResponse);

        mockMvc.perform(patch("/api/rentals/100/status")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "APPROVED",
                                  "ownerResponseNote": "Approved"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("APPROVED"));
    }

    @Test
    void cancelShouldReturnCancelledRental()
            throws Exception {

        RentalResponse cancelledResponse =
                RentalResponse.builder()
                        .id(100L)
                        .status(RentalStatus.CANCELLED)
                        .build();

        when(rentalService.cancel(
                "farmer@example.com",
                100L
        )).thenReturn(cancelledResponse);

        mockMvc.perform(patch("/api/rentals/100/cancel")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("CANCELLED"));
    }

    @Test
    void createShouldRejectInvalidRequest()
            throws Exception {

        mockMvc.perform(post("/api/rentals")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "equipmentId": null,
                                  "startDate": null,
                                  "endDate": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"));
    }

    @Test
    void cancelShouldReturnNotFound()
            throws Exception {

        when(rentalService.cancel(
                "farmer@example.com",
                999L
        )).thenThrow(new RentalNotFoundException(999L));

        mockMvc.perform(patch("/api/rentals/999/cancel")
                        .principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(
                                "Rental request not found with ID: 999"
                        ));
    }

    private RentalResponse createResponse() {
        return RentalResponse.builder()
                .id(100L)
                .equipmentId(10L)
                .equipmentName("Mahindra Tractor")
                .farmerId(1L)
                .farmerName("Test Farmer")
                .ownerId(2L)
                .ownerName("Equipment Owner")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(3))
                .totalDays(3)
                .rentalPricePerDay(
                        new BigDecimal("2500.00")
                )
                .totalAmount(
                        new BigDecimal("7500.00")
                )
                .status(RentalStatus.PENDING)
                .farmerNote("Need for land preparation")
                .build();
    }

    private String validRentalRequestJson() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);

        return """
                {
                  "equipmentId": 10,
                  "startDate": "%s",
                  "endDate": "%s",
                  "farmerNote": "Need for land preparation"
                }
                """.formatted(startDate, endDate);
    }
}