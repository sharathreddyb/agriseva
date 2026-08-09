package com.agriseva.order.repository;

import com.agriseva.order.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductOrderRepository
        extends JpaRepository<ProductOrder, Long> {

    List<ProductOrder> findByBuyerIdOrderByCreatedAtDesc(
            Long buyerId
    );

    List<ProductOrder>
    findByProductSellerIdOrderByCreatedAtDesc(
            Long sellerId
    );

    Optional<ProductOrder> findByIdAndBuyerId(
            Long orderId,
            Long buyerId
    );

    Optional<ProductOrder> findByIdAndProductSellerId(
            Long orderId,
            Long sellerId
    );
}