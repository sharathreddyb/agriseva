package com.agriseva.product.controller;

import com.agriseva.common.exception.GlobalExceptionHandler;
import com.agriseva.product.dto.ProductResponse;
import com.agriseva.product.exception.ProductNotFoundException;
import com.agriseva.product.model.ProductCategory;
import com.agriseva.product.model.ProductUnit;
import com.agriseva.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ProductController controller =
                new ProductController(productService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();
    }

    @Test
    void createShouldReturnCreatedProduct()
            throws Exception {

        ProductResponse response = createResponse();

        when(productService.create(
                eq("sharath.test@example.com"),
                any()
        )).thenReturn(response);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "sharath.test@example.com",
                        null
                );

        mockMvc.perform(post("/api/products")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.name")
                        .value("Organic Fertilizer"))
                .andExpect(jsonPath("$.category")
                        .value("FERTILIZER"))
                .andExpect(jsonPath("$.unit")
                        .value("PACK"))
                .andExpect(jsonPath("$.price")
                        .value(750.00));
    }

    @Test
    void getAllActiveProductsShouldReturnProductList()
            throws Exception {

        when(productService.getAllActiveProducts())
                .thenReturn(List.of(createResponse()));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(20))
                .andExpect(jsonPath("$[0].name")
                        .value("Organic Fertilizer"))
                .andExpect(jsonPath("$[0].category")
                        .value("FERTILIZER"));
    }

    @Test
    void getByIdShouldReturnNotFound()
            throws Exception {

        when(productService.getById(99L))
                .thenThrow(
                        new ProductNotFoundException(99L)
                );

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Product not found with ID: 99"
                        ));
    }

    @Test
    void createShouldRejectInvalidRequest()
            throws Exception {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "sharath.test@example.com",
                        null
                );

        mockMvc.perform(post("/api/products")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "category": null,
                                  "price": 0,
                                  "stockQuantity": -1,
                                  "unit": null,
                                  "serviceAddress": "",
                                  "village": "",
                                  "district": "",
                                  "state": "",
                                  "postalCode": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(
                                "Request validation failed"
                        ));
    }

    @Test
    void getMyProductsShouldReturnSellerProducts()
            throws Exception {

        when(productService.getMyProducts(
                "sharath.test@example.com"
        )).thenReturn(List.of(createResponse()));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "sharath.test@example.com",
                        null
                );

        mockMvc.perform(get("/api/products/mine")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(20))
                .andExpect(jsonPath("$[0].sellerId")
                        .value(1));
    }

    private ProductResponse createResponse() {
        return ProductResponse.builder()
                .id(20L)
                .sellerId(1L)
                .sellerName("Sharath Reddy")
                .name("Organic Fertilizer")
                .category(ProductCategory.FERTILIZER)
                .description(
                        "Organic fertilizer for crops"
                )
                .price(new BigDecimal("750.00"))
                .stockQuantity(25)
                .unit(ProductUnit.PACK)
                .imageUrl(
                        "https://example.com/fertilizer.jpg"
                )
                .serviceAddress("Main Road")
                .village("Chinnagundavelly")
                .district("Siddipet")
                .state("Telangana")
                .postalCode("502103")
                .active(true)
                .build();
    }

    private String validRequestJson() {
        return """
                {
                  "name": "Organic Fertilizer",
                  "category": "FERTILIZER",
                  "description": "Organic fertilizer for crops",
                  "price": 750.00,
                  "stockQuantity": 25,
                  "unit": "PACK",
                  "imageUrl": "https://example.com/fertilizer.jpg",
                  "serviceAddress": "Main Road",
                  "village": "Chinnagundavelly",
                  "district": "Siddipet",
                  "state": "Telangana",
                  "postalCode": "502103"
                }
                """;
    }
}