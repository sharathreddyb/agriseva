package com.agriseva.product.repository;

import com.agriseva.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrueOrderByCreatedAtDesc();

    List<Product> findBySellerIdOrderByCreatedAtDesc(
            Long sellerId
    );

    Optional<Product> findByIdAndActiveTrue(
            Long productId
    );

    Optional<Product> findByIdAndSellerId(
            Long productId,
            Long sellerId
    );
}