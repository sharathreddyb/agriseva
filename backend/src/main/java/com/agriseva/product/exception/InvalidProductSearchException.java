package com.agriseva.product.exception;

public class InvalidProductSearchException
        extends RuntimeException {

    public InvalidProductSearchException(String message) {
        super(message);
    }
}