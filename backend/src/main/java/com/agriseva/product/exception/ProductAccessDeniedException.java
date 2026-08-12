package com.agriseva.product.exception;

public class ProductAccessDeniedException extends RuntimeException {

    public ProductAccessDeniedException() {
        super("You are not allowed to manage this product");
    }
}