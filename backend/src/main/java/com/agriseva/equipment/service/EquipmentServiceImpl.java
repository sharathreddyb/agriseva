package com.agriseva.equipment.service;

import com.agriseva.equipment.dto.EquipmentRequest;
import com.agriseva.equipment.dto.EquipmentResponse;
import com.agriseva.equipment.exception.EquipmentAccessDeniedException;
import com.agriseva.equipment.exception.EquipmentNotFoundException;
import com.agriseva.equipment.exception.EquipmentOwnerRoleRequiredException;
import com.agriseva.equipment.model.Equipment;
import com.agriseva.equipment.model.EquipmentStatus;
import com.agriseva.equipment.repository.EquipmentRepository;
import com.agriseva.user.model.RoleType;
import com.agriseva.user.model.User;
import com.agriseva.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EquipmentResponse create(
            String authenticatedEmail,
            EquipmentRequest request
    ) {
        User owner = getAuthenticatedUser(authenticatedEmail);
        verifyEquipmentOwnerRole(owner);

        Equipment equipment = new Equipment();
        equipment.setOwner(owner);
        updateEquipmentFields(equipment, request);

        Equipment savedEquipment =
                equipmentRepository.save(equipment);

        return buildResponse(savedEquipment);
    }

    @Override
    @Transactional
    public EquipmentResponse update(
            String authenticatedEmail,
            Long equipmentId,
            EquipmentRequest request
    ) {
        User owner = getAuthenticatedUser(authenticatedEmail);
        verifyEquipmentOwnerRole(owner);

        Equipment equipment = equipmentRepository
                .findById(equipmentId)
                .orElseThrow(() ->
                        new EquipmentNotFoundException(equipmentId)
                );

        verifyOwnership(equipment, owner);
        updateEquipmentFields(equipment, request);

        Equipment savedEquipment =
                equipmentRepository.save(equipment);

        return buildResponse(savedEquipment);
    }

    @Override
    @Transactional
    public void deactivate(
            String authenticatedEmail,
            Long equipmentId
    ) {
        User owner = getAuthenticatedUser(authenticatedEmail);
        verifyEquipmentOwnerRole(owner);

        Equipment equipment = equipmentRepository
                .findById(equipmentId)
                .orElseThrow(() ->
                        new EquipmentNotFoundException(equipmentId)
                );

        verifyOwnership(equipment, owner);

        equipment.setActive(false);
        equipment.setStatus(EquipmentStatus.UNAVAILABLE);

        equipmentRepository.save(equipment);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentResponse getById(Long equipmentId) {
        Equipment equipment = equipmentRepository
                .findByIdAndActiveTrue(equipmentId)
                .orElseThrow(() ->
                        new EquipmentNotFoundException(equipmentId)
                );

        return buildResponse(equipment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentResponse> getAllActive() {
        return equipmentRepository
                .findByActiveTrue()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentResponse> getMyEquipment(
            String authenticatedEmail
    ) {
        User owner = getAuthenticatedUser(authenticatedEmail);

        return equipmentRepository
                .findByOwnerId(owner.getId())
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    private User getAuthenticatedUser(String authenticatedEmail) {
        return userRepository
                .findByEmailIgnoreCase(authenticatedEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user was not found"
                        )
                );
    }

    private void verifyEquipmentOwnerRole(User user) {
        boolean hasEquipmentOwnerRole = user.getRoles()
                .stream()
                .anyMatch(role ->
                        role.getName()
                                == RoleType.EQUIPMENT_OWNER
                );

        if (!hasEquipmentOwnerRole) {
            throw new EquipmentOwnerRoleRequiredException();
        }
    }

    private void verifyOwnership(
            Equipment equipment,
            User authenticatedUser
    ) {
        if (!equipment.getOwner().getId()
                .equals(authenticatedUser.getId())) {
            throw new EquipmentAccessDeniedException();
        }
    }

    private void updateEquipmentFields(
            Equipment equipment,
            EquipmentRequest request
    ) {
        equipment.setName(request.getName().trim());
        equipment.setCategory(request.getCategory());
        equipment.setDescription(
                trimToNull(request.getDescription())
        );
        equipment.setRentalPricePerDay(
                request.getRentalPricePerDay()
        );

        BigDecimal securityDeposit =
                request.getSecurityDeposit() == null
                        ? BigDecimal.ZERO
                        : request.getSecurityDeposit();

        equipment.setSecurityDeposit(securityDeposit);

        EquipmentStatus status =
                request.getStatus() == null
                        ? EquipmentStatus.AVAILABLE
                        : request.getStatus();

        equipment.setStatus(status);
        equipment.setServiceAddress(
                request.getServiceAddress().trim()
        );
        equipment.setVillage(request.getVillage().trim());
        equipment.setDistrict(request.getDistrict().trim());
        equipment.setState(request.getState().trim());
        equipment.setPostalCode(
                request.getPostalCode().trim()
        );
        equipment.setImageUrl(
                trimToNull(request.getImageUrl())
        );
    }

    private EquipmentResponse buildResponse(
            Equipment equipment
    ) {
        return EquipmentResponse.builder()
                .id(equipment.getId())
                .ownerId(equipment.getOwner().getId())
                .ownerName(equipment.getOwner().getFullName())
                .name(equipment.getName())
                .category(equipment.getCategory())
                .description(equipment.getDescription())
                .rentalPricePerDay(
                        equipment.getRentalPricePerDay()
                )
                .securityDeposit(
                        equipment.getSecurityDeposit()
                )
                .status(equipment.getStatus())
                .serviceAddress(
                        equipment.getServiceAddress()
                )
                .village(equipment.getVillage())
                .district(equipment.getDistrict())
                .state(equipment.getState())
                .postalCode(equipment.getPostalCode())
                .imageUrl(equipment.getImageUrl())
                .active(equipment.isActive())
                .createdAt(equipment.getCreatedAt())
                .updatedAt(equipment.getUpdatedAt())
                .build();
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}