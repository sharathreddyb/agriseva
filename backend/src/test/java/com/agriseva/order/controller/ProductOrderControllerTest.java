package com.agriseva.order.controller;

import com.agriseva.common.exception.GlobalExceptionHandler;
import com.agriseva.order.dto.ProductOrderResponse;
import com.agriseva.order.exception.InsufficientProductStockException;
import com.agriseva.order.exception.ProductOrderNotFoundException;
import com.agriseva.order.model.OrderStatus;
import com.agriseva.order.service.ProductOrderService;
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
class ProductOrderControllerTest {

    @Mock
    private ProductOrderService orderService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ProductOrderController controller =
                new ProductOrderController(orderService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();
    }

    @Test
    void createShouldReturnCreatedOrder()
            throws Exception {

        ProductOrderResponse response = createResponse();

        when(orderService.create(
                eq("buyer@example.com"),
                any()
        )).thenReturn(response);

        UsernamePasswordAuthenticationToken authentication =
                authentication("buyer@example.com");

        mockMvc.perform(post("/api/orders")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 20,
                                  "quantity": 3,
                                  "buyerNote": "Please deliver carefully"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.productId").value(20))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.status")
                        .value("PENDING"))
                .andExpect(jsonPath("$.totalAmount")
                        .value(2250.00));
    }

    @Test
    void createShouldRejectInvalidRequest()
            throws Exception {

        UsernamePasswordAuthenticationToken authentication =
                authentication("buyer@example.com");

        mockMvc.perform(post("/api/orders")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": null,
                                  "quantity": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Request validation failed"));
    }

    @Test
    void getMyOrdersShouldReturnOrders()
            throws Exception {

        when(orderService.getMyOrders(
                "buyer@example.com"
        )).thenReturn(List.of(createResponse()));

        UsernamePasswordAuthenticationToken authentication =
                authentication("buyer@example.com");

        mockMvc.perform(get("/api/orders/mine")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(100))
                .andExpect(jsonPath("$[0].productName")
                        .value("Organic Fertilizer"));
    }

    @Test
    void getReceivedOrdersShouldReturnOrders()
            throws Exception {

        when(orderService.getReceivedOrders(
                "seller@example.com"
        )).thenReturn(List.of(createResponse()));

        UsernamePasswordAuthenticationToken authentication =
                authentication("seller@example.com");

        mockMvc.perform(get("/api/orders/received")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(100))
                .andExpect(jsonPath("$[0].sellerId")
                        .value(1));
    }

    @Test
    void sellerShouldUpdateOrderStatus()
            throws Exception {

        ProductOrderResponse response =
                createResponseWithStatus(
                        OrderStatus.ACCEPTED
                );

        when(orderService.updateStatus(
                eq("seller@example.com"),
                eq(100L),
                any()
        )).thenReturn(response);

        UsernamePasswordAuthenticationToken authentication =
                authentication("seller@example.com");

        mockMvc.perform(
                        put("/api/orders/100/status")
                                .principal(authentication)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "status": "ACCEPTED",
                                          "sellerResponseNote": "Order accepted"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("ACCEPTED"));
    }

    @Test
    void buyerShouldCancelOrder()
            throws Exception {

        ProductOrderResponse response =
                createResponseWithStatus(
                        OrderStatus.CANCELLED
                );

        when(orderService.cancel(
                "buyer@example.com",
                100L
        )).thenReturn(response);

        UsernamePasswordAuthenticationToken authentication =
                authentication("buyer@example.com");

        mockMvc.perform(
                        put("/api/orders/100/cancel")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("CANCELLED"));
    }

    @Test
    void missingOrderShouldReturnNotFound()
            throws Exception {

        when(orderService.cancel(
                "buyer@example.com",
                999L
        )).thenThrow(
                new ProductOrderNotFoundException(999L)
        );

        UsernamePasswordAuthenticationToken authentication =
                authentication("buyer@example.com");

        mockMvc.perform(
                        put("/api/orders/999/cancel")
                                .principal(authentication)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Product order not found with ID: 999"
                        ));
    }

    @Test
    void insufficientStockShouldReturnBadRequest()
            throws Exception {

        when(orderService.create(
                eq("buyer@example.com"),
                any()
        )).thenThrow(
                new InsufficientProductStockException()
        );

        UsernamePasswordAuthenticationToken authentication =
                authentication("buyer@example.com");

        mockMvc.perform(post("/api/orders")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 20,
                                  "quantity": 100
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(
                                "Requested quantity is not available in stock"
                        ));
    }

    private UsernamePasswordAuthenticationToken authentication(
            String email
    ) {
        return new UsernamePasswordAuthenticationToken(
                email,
                null
        );
    }

    private ProductOrderResponse createResponse() {
        return createResponseWithStatus(
                OrderStatus.PENDING
        );
    }

    private ProductOrderResponse createResponseWithStatus(
            OrderStatus status
    ) {
        return ProductOrderResponse.builder()
                .id(100L)
                .productId(20L)
                .productName("Organic Fertilizer")
                .buyerId(2L)
                .buyerName("Test Buyer")
                .sellerId(1L)
                .sellerName("Test Seller")
                .quantity(3)
                .unitPrice(
                        new BigDecimal("750.00")
                )
                .totalAmount(
                        new BigDecimal("2250.00")
                )
                .status(status)
                .buyerNote(
                        "Please deliver carefully"
                )
                .build();
    }
}