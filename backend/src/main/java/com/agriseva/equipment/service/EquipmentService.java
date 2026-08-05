package com.agriseva.equipment.service;

import com.agriseva.equipment.dto.EquipmentRequest;
import com.agriseva.equipment.dto.EquipmentResponse;
import com.agriseva.equipment.model.EquipmentCategory;
import com.agriseva.equipment.model.EquipmentStatus;

import java.util.List;

public interface EquipmentService {

    EquipmentResponse create(
            String authenticatedEmail,
            EquipmentRequest request
    );

    EquipmentResponse update(
            String authenticatedEmail,
            Long equipmentId,
            EquipmentRequest request
    );

    void deactivate(
            String authenticatedEmail,
            Long equipmentId
    );

    EquipmentResponse getById(Long equipmentId);

    List<EquipmentResponse> search(
            EquipmentCategory category,
            String district,
            String village,
            EquipmentStatus status,
            String keyword
    );

    List<EquipmentResponse> getMyEquipment(
            String authenticatedEmail
    );
}