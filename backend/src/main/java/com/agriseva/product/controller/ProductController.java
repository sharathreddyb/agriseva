package com.agriseva.product.controller;

import com.agriseva.product.dto.ProductRequest;
import com.agriseva.product.dto.ProductResponse;
import com.agriseva.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            Authentication authentication,
            @Valid @RequestBody ProductRequest request
    ) {
        ProductResponse response = productService.create(
                authentication.getName(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> update(
            Authentication authentication,
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequest request
    ) {
        ProductResponse response = productService.update(
                authentication.getName(),
                productId,
                request
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deactivate(
            Authentication authentication,
            @PathVariable Long productId
    ) {
        productService.deactivate(
                authentication.getName(),
                productId
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getById(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                productService.getById(productId)
        );
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>>
    getAllActiveProducts() {
        return ResponseEntity.ok(
                productService.getAllActiveProducts()
        );
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ProductResponse>> getMyProducts(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                productService.getMyProducts(
                        authentication.getName()
                )
        );
    }
}