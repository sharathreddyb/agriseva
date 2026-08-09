package com.agriseva.product.specification;

import com.agriseva.product.model.Product;
import com.agriseva.product.model.ProductCategory;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("active"));
    }

    public static Specification<Product> hasCategory(
            ProductCategory category
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

    public static Specification<Product> hasDistrict(
            String district
    ) {
        return (root, query, criteriaBuilder) -> {
            if (district == null || district.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(
                            root.get("district")
                    ),
                    district.trim().toLowerCase()
            );
        };
    }

    public static Specification<Product> hasVillage(
            String village
    ) {
        return (root, query, criteriaBuilder) -> {
            if (village == null || village.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(
                            root.get("village")
                    ),
                    village.trim().toLowerCase()
            );
        };
    }

    public static Specification<Product> nameContains(
            String keyword
    ) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String searchPattern =
                    "%" + keyword.trim().toLowerCase() + "%";

            return criteriaBuilder.like(
                    criteriaBuilder.lower(
                            root.get("name")
                    ),
                    searchPattern
            );
        };
    }

    public static Specification<Product> priceGreaterThanOrEqualTo(
            BigDecimal minPrice
    ) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("price"),
                    minPrice
            );
        };
    }

    public static Specification<Product> priceLessThanOrEqualTo(
            BigDecimal maxPrice
    ) {
        return (root, query, criteriaBuilder) -> {
            if (maxPrice == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("price"),
                    maxPrice
            );
        };
    }

    public static Specification<Product> isInStock(
            Boolean inStock
    ) {
        return (root, query, criteriaBuilder) -> {
            if (inStock == null || !inStock) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThan(
                    root.get("stockQuantity"),
                    0
            );
        };
    }
}