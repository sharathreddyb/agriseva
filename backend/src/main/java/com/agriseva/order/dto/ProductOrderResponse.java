package com.agriseva.order.dto;

import com.agriseva.order.model.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProductOrderResponse {

    private Long id;

    private Long productId;
    private String productName;

    private Long buyerId;
    private String buyerName;

    private Long sellerId;
    private String sellerName;

    private Integer quantity;

    private BigDecimal unitPrice;
    private BigDecimal totalAmount;

    private OrderStatus status;

    private String buyerNote;
    private String sellerResponseNote;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}