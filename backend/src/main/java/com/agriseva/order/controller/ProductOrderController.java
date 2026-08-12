package com.agriseva.order.controller;

import com.agriseva.order.dto.OrderStatusUpdateRequest;
import com.agriseva.order.dto.ProductOrderRequest;
import com.agriseva.order.dto.ProductOrderResponse;
import com.agriseva.order.service.ProductOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class ProductOrderController {

    private final ProductOrderService orderService;

    @PostMapping
    public ResponseEntity<ProductOrderResponse> create(
            Authentication authentication,
            @Valid @RequestBody ProductOrderRequest request
    ) {
        ProductOrderResponse response =
                orderService.create(
                        authentication.getName(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ProductOrderResponse>>
    getMyOrders(Authentication authentication) {

        return ResponseEntity.ok(
                orderService.getMyOrders(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/received")
    public ResponseEntity<List<ProductOrderResponse>>
    getReceivedOrders(Authentication authentication) {

        return ResponseEntity.ok(
                orderService.getReceivedOrders(
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ProductOrderResponse> updateStatus(
            Authentication authentication,
            @PathVariable Long orderId,
            @Valid @RequestBody
            OrderStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(
                orderService.updateStatus(
                        authentication.getName(),
                        orderId,
                        request
                )
        );
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ProductOrderResponse> cancel(
            Authentication authentication,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(
                orderService.cancel(
                        authentication.getName(),
                        orderId
                )
        );
    }
}