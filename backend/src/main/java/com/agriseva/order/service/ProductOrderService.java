package com.agriseva.order.service;

import com.agriseva.order.dto.OrderStatusUpdateRequest;
import com.agriseva.order.dto.ProductOrderRequest;
import com.agriseva.order.dto.ProductOrderResponse;

import java.util.List;

public interface ProductOrderService {

    ProductOrderResponse create(
            String authenticatedEmail,
            ProductOrderRequest request
    );

    List<ProductOrderResponse> getMyOrders(
            String authenticatedEmail
    );

    List<ProductOrderResponse> getReceivedOrders(
            String authenticatedEmail
    );

    ProductOrderResponse updateStatus(
            String authenticatedEmail,
            Long orderId,
            OrderStatusUpdateRequest request
    );

    ProductOrderResponse cancel(
            String authenticatedEmail,
            Long orderId
    );
}