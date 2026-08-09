package com.agriseva.order.exception;

public class ProductOrderAccessDeniedException
        extends RuntimeException {

    public ProductOrderAccessDeniedException() {
        super(
                "You are not allowed to manage this product order"
        );
    }
}