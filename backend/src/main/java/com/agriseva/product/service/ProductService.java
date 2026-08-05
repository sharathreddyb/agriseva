package com.agriseva.product.service;

import com.agriseva.product.dto.ProductRequest;
import com.agriseva.product.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse create(
            String authenticatedEmail,
            ProductRequest request
    );

    ProductResponse update(
            String authenticatedEmail,
            Long productId,
            ProductRequest request
    );

    void deactivate(
            String authenticatedEmail,
            Long productId
    );

    ProductResponse getById(Long productId);

    List<ProductResponse> getAllActiveProducts();

    List<ProductResponse> getMyProducts(
            String authenticatedEmail
    );
}