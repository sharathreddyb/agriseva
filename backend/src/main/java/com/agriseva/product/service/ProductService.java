package com.agriseva.product.service;

import com.agriseva.product.dto.ProductRequest;
import com.agriseva.product.dto.ProductResponse;
import com.agriseva.product.model.ProductCategory;

import java.math.BigDecimal;
import java.util.List;

import com.agriseva.product.model.ProductCategory;

import java.math.BigDecimal;


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

    List<ProductResponse> search(
            ProductCategory category,
            String district,
            String village,
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock
    );

    List<ProductResponse> getMyProducts(
            String authenticatedEmail
    );
}