package com.agriseva.rental.controller;

import com.agriseva.rental.dto.RentalRequest;
import com.agriseva.rental.dto.RentalResponse;
import com.agriseva.rental.dto.RentalStatusUpdateRequest;
import com.agriseva.rental.service.EquipmentRentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class EquipmentRentalController {

    private final EquipmentRentalService rentalService;

    @PostMapping
    public ResponseEntity<RentalResponse> create(
            Authentication authentication,
            @Valid @RequestBody RentalRequest request
    ) {
        RentalResponse response = rentalService.create(
                authentication.getName(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<RentalResponse>> getMyRentals(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                rentalService.getMyRentals(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/owner")
    public ResponseEntity<List<RentalResponse>> getOwnerRequests(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                rentalService.getOwnerRequests(
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{rentalId}/status")
    public ResponseEntity<RentalResponse> updateStatus(
            Authentication authentication,
            @PathVariable Long rentalId,
            @Valid @RequestBody RentalStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(
                rentalService.updateStatus(
                        authentication.getName(),
                        rentalId,
                        request
                )
        );
    }

    @PatchMapping("/{rentalId}/cancel")
    public ResponseEntity<RentalResponse> cancel(
            Authentication authentication,
            @PathVariable Long rentalId
    ) {
        return ResponseEntity.ok(
                rentalService.cancel(
                        authentication.getName(),
                        rentalId
                )
        );
    }
}