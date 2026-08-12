package com.agriseva.equipment.repository;

import com.agriseva.equipment.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository
        extends JpaRepository<Equipment, Long>,
        JpaSpecificationExecutor<Equipment> {

    List<Equipment> findByActiveTrue();

    List<Equipment> findByOwnerId(Long ownerId);

    Optional<Equipment> findByIdAndActiveTrue(Long id);

    Optional<Equipment> findByIdAndOwnerId(
            Long id,
            Long ownerId
    );
}