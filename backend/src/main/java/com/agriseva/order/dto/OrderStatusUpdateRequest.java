package com.agriseva.order.dto;

import com.agriseva.order.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusUpdateRequest {

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    @Size(
            max = 500,
            message = "Seller response note must not exceed 500 characters"
    )
    private String sellerResponseNote;
}