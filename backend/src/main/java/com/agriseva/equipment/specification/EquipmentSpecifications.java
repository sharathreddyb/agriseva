package com.agriseva.equipment.specification;

import com.agriseva.equipment.model.Equipment;
import com.agriseva.equipment.model.EquipmentCategory;
import com.agriseva.equipment.model.EquipmentStatus;
import org.springframework.data.jpa.domain.Specification;

public final class EquipmentSpecifications {

    private EquipmentSpecifications() {
    }

    public static Specification<Equipment> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("active"));
    }

    public static Specification<Equipment> hasCategory(
            EquipmentCategory category
    ) {
        return (root, query, criteriaBuilder) -> {
            if (category == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("category"),
                    category
            );
        };
    }

    public static Specification<Equipment> hasStatus(
            EquipmentStatus status
    ) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("status"),
                    status
            );
        };
    }

    public static Specification<Equipment> hasDistrict(
            String district
    ) {
        return (root, query, criteriaBuilder) -> {
            if (district == null || district.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("district")),
                    district.trim().toLowerCase()
            );
        };
    }

    public static Specification<Equipment> hasVillage(
            String village
    ) {
        return (root, query, criteriaBuilder) -> {
            if (village == null || village.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("village")),
                    village.trim().toLowerCase()
            );
        };
    }

    public static Specification<Equipment> nameContains(
            String keyword
    ) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String searchPattern =
                    "%" + keyword.trim().toLowerCase() + "%";

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    searchPattern
            );
        };
    }
}