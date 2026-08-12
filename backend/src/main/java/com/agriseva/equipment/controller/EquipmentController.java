package com.agriseva.equipment.controller;

import com.agriseva.equipment.dto.EquipmentRequest;
import com.agriseva.equipment.dto.EquipmentResponse;
import com.agriseva.equipment.model.EquipmentCategory;
import com.agriseva.equipment.model.EquipmentStatus;
import com.agriseva.equipment.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.agriseva.equipment.model.EquipmentCategory;
import com.agriseva.equipment.model.EquipmentStatus;

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
    public ResponseEntity<List<EquipmentResponse>> search(
            @RequestParam(required = false)
            EquipmentCategory category,
    
            @RequestParam(required = false)
            String district,
    
            @RequestParam(required = false)
            String village,
    
            @RequestParam(required = false)
            EquipmentStatus status,
    
            @RequestParam(required = false)
            String keyword
    ) {
        return ResponseEntity.ok(
                equipmentService.search(
                        category,
                        district,
                        village,
                        status,
                        keyword
                )
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