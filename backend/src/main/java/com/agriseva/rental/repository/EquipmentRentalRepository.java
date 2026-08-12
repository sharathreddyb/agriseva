package com.agriseva.rental.repository;

import com.agriseva.rental.model.EquipmentRental;
import com.agriseva.rental.model.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipmentRentalRepository
        extends JpaRepository<EquipmentRental, Long> {

    List<EquipmentRental> findByFarmerIdOrderByCreatedAtDesc(
            Long farmerId
    );

    List<EquipmentRental>
    findByEquipmentOwnerIdOrderByCreatedAtDesc(
            Long ownerId
    );

    Optional<EquipmentRental> findByIdAndFarmerId(
            Long rentalId,
            Long farmerId
    );

    Optional<EquipmentRental> findByIdAndEquipmentOwnerId(
            Long rentalId,
            Long ownerId
    );

    boolean existsByEquipmentIdAndStatusIn(
            Long equipmentId,
            List<RentalStatus> statuses
    );
}