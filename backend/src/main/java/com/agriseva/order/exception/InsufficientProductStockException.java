package com.agriseva.order.exception;

public class InsufficientProductStockException
        extends RuntimeException {

    public InsufficientProductStockException() {
        super(
                "Requested quantity is not available in stock"
        );
    }
}