package com.agriseva.order.exception;

public class ProductOrderNotFoundException
        extends RuntimeException {

    public ProductOrderNotFoundException(Long orderId) {
        super(
                "Product order not found with ID: "
                        + orderId
        );
    }
}