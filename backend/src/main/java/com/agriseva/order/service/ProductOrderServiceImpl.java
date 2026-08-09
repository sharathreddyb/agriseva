package com.agriseva.order.service;

import com.agriseva.order.dto.OrderStatusUpdateRequest;
import com.agriseva.order.dto.ProductOrderRequest;
import com.agriseva.order.dto.ProductOrderResponse;
import com.agriseva.order.exception.InsufficientProductStockException;
import com.agriseva.order.exception.InvalidOrderStatusException;
import com.agriseva.order.exception.ProductOrderAccessDeniedException;
import com.agriseva.order.exception.ProductOrderNotFoundException;
import com.agriseva.order.model.OrderStatus;
import com.agriseva.order.model.ProductOrder;
import com.agriseva.order.repository.ProductOrderRepository;
import com.agriseva.product.exception.ProductNotFoundException;
import com.agriseva.product.model.Product;
import com.agriseva.product.repository.ProductRepository;
import com.agriseva.user.model.User;
import com.agriseva.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductOrderServiceImpl
        implements ProductOrderService {

    private final ProductOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProductOrderResponse create(
            String authenticatedEmail,
            ProductOrderRequest request
    ) {
        User buyer =
                getAuthenticatedUser(authenticatedEmail);

        Product product = productRepository
                .findByIdAndActiveTrue(request.getProductId())
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                request.getProductId()
                        )
                );

        if (product.getSeller()
                .getId()
                .equals(buyer.getId())) {
            throw new ProductOrderAccessDeniedException();
        }

        if (product.getStockQuantity()
                < request.getQuantity()) {
            throw new InsufficientProductStockException();
        }

        BigDecimal unitPrice = product.getPrice();

        BigDecimal totalAmount =
                unitPrice.multiply(
                        BigDecimal.valueOf(
                                request.getQuantity()
                        )
                );

        ProductOrder order = new ProductOrder();
        order.setProduct(product);
        order.setBuyer(buyer);
        order.setQuantity(request.getQuantity());
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setBuyerNote(
                trimToNull(request.getBuyerNote())
        );

        product.setStockQuantity(
                product.getStockQuantity()
                        - request.getQuantity()
        );

        productRepository.save(product);

        ProductOrder savedOrder =
                orderRepository.save(order);

        return buildResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductOrderResponse> getMyOrders(
            String authenticatedEmail
    ) {
        User buyer =
                getAuthenticatedUser(authenticatedEmail);

        return orderRepository
                .findByBuyerIdOrderByCreatedAtDesc(
                        buyer.getId()
                )
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductOrderResponse> getReceivedOrders(
            String authenticatedEmail
    ) {
        User seller =
                getAuthenticatedUser(authenticatedEmail);

        return orderRepository
                .findByProductSellerIdOrderByCreatedAtDesc(
                        seller.getId()
                )
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductOrderResponse updateStatus(
            String authenticatedEmail,
            Long orderId,
            OrderStatusUpdateRequest request
    ) {
        User seller =
                getAuthenticatedUser(authenticatedEmail);

        ProductOrder order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ProductOrderNotFoundException(
                                orderId
                        )
                );

        verifySellerOwnership(order, seller);

        OrderStatus requestedStatus =
                request.getStatus();

        validateSellerStatusChange(
                order,
                requestedStatus
        );

        if (requestedStatus == OrderStatus.REJECTED) {
            restoreStock(order);
        }

        order.setStatus(requestedStatus);
        order.setSellerResponseNote(
                trimToNull(
                        request.getSellerResponseNote()
                )
        );

        ProductOrder savedOrder =
                orderRepository.save(order);

        return buildResponse(savedOrder);
    }

    @Override
    @Transactional
    public ProductOrderResponse cancel(
            String authenticatedEmail,
            Long orderId
    ) {
        User buyer =
                getAuthenticatedUser(authenticatedEmail);

        ProductOrder order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ProductOrderNotFoundException(
                                orderId
                        )
                );

        verifyBuyerOwnership(order, buyer);

        if (order.getStatus()
                != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException(
                    "Only pending orders can be cancelled"
            );
        }

        restoreStock(order);

        order.setStatus(OrderStatus.CANCELLED);

        ProductOrder savedOrder =
                orderRepository.save(order);

        return buildResponse(savedOrder);
    }

    private void validateSellerStatusChange(
            ProductOrder order,
            OrderStatus requestedStatus
    ) {
        OrderStatus currentStatus =
                order.getStatus();

        if (currentStatus == OrderStatus.PENDING
                && (requestedStatus == OrderStatus.ACCEPTED
                || requestedStatus == OrderStatus.REJECTED)) {
            return;
        }

        if (currentStatus == OrderStatus.ACCEPTED
                && requestedStatus == OrderStatus.COMPLETED) {
            return;
        }

        throw new InvalidOrderStatusException(
                "Invalid product order status change"
        );
    }

    private void restoreStock(ProductOrder order) {
        Product product = order.getProduct();

        product.setStockQuantity(
                product.getStockQuantity()
                        + order.getQuantity()
        );

        productRepository.save(product);
    }

    private void verifyBuyerOwnership(
            ProductOrder order,
            User buyer
    ) {
        if (!order.getBuyer()
                .getId()
                .equals(buyer.getId())) {
            throw new ProductOrderAccessDeniedException();
        }
    }

    private void verifySellerOwnership(
            ProductOrder order,
            User seller
    ) {
        if (!order.getProduct()
                .getSeller()
                .getId()
                .equals(seller.getId())) {
            throw new ProductOrderAccessDeniedException();
        }
    }

    private User getAuthenticatedUser(
            String authenticatedEmail
    ) {
        return userRepository
                .findByEmailIgnoreCase(authenticatedEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user was not found"
                        )
                );
    }

    private ProductOrderResponse buildResponse(
            ProductOrder order
    ) {
        Product product = order.getProduct();

        return ProductOrderResponse.builder()
                .id(order.getId())
                .productId(product.getId())
                .productName(product.getName())
                .buyerId(order.getBuyer().getId())
                .buyerName(
                        order.getBuyer().getFullName()
                )
                .sellerId(
                        product.getSeller().getId()
                )
                .sellerName(
                        product.getSeller().getFullName()
                )
                .quantity(order.getQuantity())
                .unitPrice(order.getUnitPrice())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .buyerNote(order.getBuyerNote())
                .sellerResponseNote(
                        order.getSellerResponseNote()
                )
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}