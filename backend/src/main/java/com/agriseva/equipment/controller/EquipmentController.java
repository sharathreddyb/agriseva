package com.agriseva.equipment.controller;

import com.agriseva.equipment.dto.EquipmentRequest;
import com.agriseva.equipment.dto.EquipmentResponse;
import com.agriseva.equipment.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping
    public ResponseEntity<EquipmentResponse> create(
            Authentication authentication,
            @Valid @RequestBody EquipmentRequest request
    ) {
        EquipmentResponse response = equipmentService.create(
                authentication.getName(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{equipmentId}")
    public ResponseEntity<EquipmentResponse> update(
            Authentication authentication,
            @PathVariable Long equipmentId,
            @Valid @RequestBody EquipmentRequest request
    ) {
        EquipmentResponse response = equipmentService.update(
                authentication.getName(),
                equipmentId,
                request
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{equipmentId}")
    public ResponseEntity<Void> deactivate(
            Authentication authentication,
            @PathVariable Long equipmentId
    ) {
        equipmentService.deactivate(
                authentication.getName(),
                equipmentId
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{equipmentId}")
    public ResponseEntity<EquipmentResponse> getById(
            @PathVariable Long equipmentId
    ) {
        return ResponseEntity.ok(
                equipmentService.getById(equipmentId)
        );
    }

    @GetMapping
    public ResponseEntity<List<EquipmentResponse>> getAllActive() {
        return ResponseEntity.ok(
                equipmentService.getAllActive()
        );
    }

    @GetMapping("/mine")
    public ResponseEntity<List<EquipmentResponse>> getMyEquipment(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                equipmentService.getMyEquipment(
                        authentication.getName()
                )
        );
    }
}