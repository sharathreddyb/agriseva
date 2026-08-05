package com.agriseva.product.exception;

public class ProductSellerRoleRequiredException
        extends RuntimeException {

    public ProductSellerRoleRequiredException() {
        super(
                "You must activate the product seller role " +
                "before managing products"
        );
    }
}