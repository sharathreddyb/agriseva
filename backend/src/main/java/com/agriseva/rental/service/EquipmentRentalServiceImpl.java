package com.agriseva.rental.service;

import com.agriseva.equipment.exception.EquipmentNotFoundException;
import com.agriseva.equipment.model.Equipment;
import com.agriseva.equipment.model.EquipmentStatus;
import com.agriseva.equipment.repository.EquipmentRepository;
import com.agriseva.rental.dto.RentalRequest;
import com.agriseva.rental.dto.RentalResponse;
import com.agriseva.rental.dto.RentalStatusUpdateRequest;
import com.agriseva.rental.exception.EquipmentUnavailableForRentalException;
import com.agriseva.rental.exception.InvalidRentalDatesException;
import com.agriseva.rental.exception.InvalidRentalStatusException;
import com.agriseva.rental.exception.RentalNotFoundException;
import com.agriseva.rental.model.EquipmentRental;
import com.agriseva.rental.model.RentalStatus;
import com.agriseva.rental.repository.EquipmentRentalRepository;
import com.agriseva.user.model.User;
import com.agriseva.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentRentalServiceImpl
        implements EquipmentRentalService {

    private final EquipmentRentalRepository rentalRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RentalResponse create(
            String authenticatedEmail,
            RentalRequest request
    ) {
        User farmer = getAuthenticatedUser(authenticatedEmail);

        Equipment equipment = equipmentRepository
                .findByIdAndActiveTrue(request.getEquipmentId())
                .orElseThrow(() ->
                        new EquipmentNotFoundException(
                                request.getEquipmentId()
                        )
                );

        validateEquipmentAvailability(equipment);
        validateRentalDates(request);

        if (equipment.getOwner().getId().equals(farmer.getId())) {
            throw new InvalidRentalStatusException(
                    "You cannot rent your own equipment"
            );
        }

        long totalDays = ChronoUnit.DAYS.between(
                request.getStartDate(),
                request.getEndDate()
        ) + 1;

        BigDecimal dailyPrice =
                equipment.getRentalPricePerDay();

        BigDecimal totalAmount = dailyPrice.multiply(
                BigDecimal.valueOf(totalDays)
        );

        EquipmentRental rental = new EquipmentRental();
        rental.setEquipment(equipment);
        rental.setFarmer(farmer);
        rental.setStartDate(request.getStartDate());
        rental.setEndDate(request.getEndDate());
        rental.setTotalDays(Math.toIntExact(totalDays));
        rental.setRentalPricePerDay(dailyPrice);
        rental.setTotalAmount(totalAmount);
        rental.setStatus(RentalStatus.PENDING);
        rental.setFarmerNote(
                trimToNull(request.getFarmerNote())
        );

        EquipmentRental savedRental =
                rentalRepository.save(rental);

        return buildResponse(savedRental);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalResponse> getMyRentals(
            String authenticatedEmail
    ) {
        User farmer = getAuthenticatedUser(authenticatedEmail);

        return rentalRepository
                .findByFarmerIdOrderByCreatedAtDesc(farmer.getId())
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalResponse> getOwnerRequests(
            String authenticatedEmail
    ) {
        User owner = getAuthenticatedUser(authenticatedEmail);

        return rentalRepository
                .findByEquipmentOwnerIdOrderByCreatedAtDesc(
                        owner.getId()
                )
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    @Transactional
    public RentalResponse updateStatus(
            String authenticatedEmail,
            Long rentalId,
            RentalStatusUpdateRequest request
    ) {
        User owner = getAuthenticatedUser(authenticatedEmail);

        EquipmentRental rental = rentalRepository
                .findByIdAndEquipmentOwnerId(
                        rentalId,
                        owner.getId()
                )
                .orElseThrow(() ->
                        new RentalNotFoundException(rentalId)
                );

        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new InvalidRentalStatusException(
                    "Only pending rental requests can be approved or rejected"
            );
        }

        if (request.getStatus() != RentalStatus.APPROVED
                && request.getStatus() != RentalStatus.REJECTED) {
            throw new InvalidRentalStatusException(
                    "Owner can only approve or reject a rental request"
            );
        }

        rental.setStatus(request.getStatus());
        rental.setOwnerResponseNote(
                trimToNull(request.getOwnerResponseNote())
        );

        EquipmentRental savedRental =
                rentalRepository.save(rental);

        return buildResponse(savedRental);
    }

    @Override
    @Transactional
    public RentalResponse cancel(
            String authenticatedEmail,
            Long rentalId
    ) {
        User farmer = getAuthenticatedUser(authenticatedEmail);

        EquipmentRental rental = rentalRepository
                .findByIdAndFarmerId(
                        rentalId,
                        farmer.getId()
                )
                .orElseThrow(() ->
                        new RentalNotFoundException(rentalId)
                );

        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new InvalidRentalStatusException(
                    "Only pending rental requests can be cancelled"
            );
        }

        rental.setStatus(RentalStatus.CANCELLED);

        EquipmentRental savedRental =
                rentalRepository.save(rental);

        return buildResponse(savedRental);
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

    private void validateEquipmentAvailability(
            Equipment equipment
    ) {
        if (!equipment.isActive()
                || equipment.getStatus()
                != EquipmentStatus.AVAILABLE) {

            throw new EquipmentUnavailableForRentalException();
        }
    }

    private void validateRentalDates(RentalRequest request) {
        if (request.getEndDate()
                .isBefore(request.getStartDate())) {

            throw new InvalidRentalDatesException();
        }
    }

    private RentalResponse buildResponse(
            EquipmentRental rental
    ) {
        Equipment equipment = rental.getEquipment();
        User owner = equipment.getOwner();
        User farmer = rental.getFarmer();

        return RentalResponse.builder()
                .id(rental.getId())
                .equipmentId(equipment.getId())
                .equipmentName(equipment.getName())
                .farmerId(farmer.getId())
                .farmerName(farmer.getFullName())
                .ownerId(owner.getId())
                .ownerName(owner.getFullName())
                .startDate(rental.getStartDate())
                .endDate(rental.getEndDate())
                .totalDays(rental.getTotalDays())
                .rentalPricePerDay(
                        rental.getRentalPricePerDay()
                )
                .totalAmount(rental.getTotalAmount())
                .status(rental.getStatus())
                .farmerNote(rental.getFarmerNote())
                .ownerResponseNote(
                        rental.getOwnerResponseNote()
                )
                .createdAt(rental.getCreatedAt())
                .updatedAt(rental.getUpdatedAt())
                .build();
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}