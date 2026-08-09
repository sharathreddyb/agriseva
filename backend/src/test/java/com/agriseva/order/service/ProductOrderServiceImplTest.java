package com.agriseva.order.service;

import com.agriseva.order.dto.OrderStatusUpdateRequest;
import com.agriseva.order.dto.ProductOrderRequest;
import com.agriseva.order.dto.ProductOrderResponse;
import com.agriseva.order.exception.InsufficientProductStockException;
import com.agriseva.order.exception.InvalidOrderStatusException;
import com.agriseva.order.exception.ProductOrderAccessDeniedException;
import com.agriseva.order.model.OrderStatus;
import com.agriseva.order.model.ProductOrder;
import com.agriseva.order.repository.ProductOrderRepository;
import com.agriseva.product.model.Product;
import com.agriseva.product.model.ProductCategory;
import com.agriseva.product.model.ProductUnit;
import com.agriseva.product.repository.ProductRepository;
import com.agriseva.user.model.User;
import com.agriseva.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductOrderServiceImplTest {

    @Mock
    private ProductOrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    private ProductOrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new ProductOrderServiceImpl(
                orderRepository,
                productRepository,
                userRepository
        );
    }

    @Test
    void createShouldPlaceOrderAndReduceStock() {
        User buyer = createBuyer();
        User seller = createSeller();
        Product product = createProduct(seller);

        when(userRepository.findByEmailIgnoreCase(
                "buyer@example.com"
        )).thenReturn(Optional.of(buyer));

        when(productRepository.findByIdAndActiveTrue(20L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(any(ProductOrder.class)))
                .thenAnswer(invocation -> {
                    ProductOrder order =
                            invocation.getArgument(0);
                    order.setId(100L);
                    return order;
                });

        ProductOrderResponse response =
                orderService.create(
                        "buyer@example.com",
                        createOrderRequest()
                );

        assertEquals(100L, response.getId());
        assertEquals(20L, response.getProductId());
        assertEquals(3, response.getQuantity());
        assertEquals(
                new BigDecimal("750.00"),
                response.getUnitPrice()
        );
        assertEquals(
                new BigDecimal("2250.00"),
                response.getTotalAmount()
        );
        assertEquals(
                OrderStatus.PENDING,
                response.getStatus()
        );

        assertEquals(22, product.getStockQuantity());

        verify(productRepository).save(product);
        verify(orderRepository)
                .save(any(ProductOrder.class));
    }

    @Test
    void createShouldRejectInsufficientStock() {
        User buyer = createBuyer();
        User seller = createSeller();
        Product product = createProduct(seller);

        product.setStockQuantity(2);

        when(userRepository.findByEmailIgnoreCase(
                "buyer@example.com"
        )).thenReturn(Optional.of(buyer));

        when(productRepository.findByIdAndActiveTrue(20L))
                .thenReturn(Optional.of(product));

        assertThrows(
                InsufficientProductStockException.class,
                () -> orderService.create(
                        "buyer@example.com",
                        createOrderRequest()
                )
        );

        verify(productRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createShouldRejectSellerBuyingOwnProduct() {
        User seller = createSeller();
        Product product = createProduct(seller);

        when(userRepository.findByEmailIgnoreCase(
                "seller@example.com"
        )).thenReturn(Optional.of(seller));

        when(productRepository.findByIdAndActiveTrue(20L))
                .thenReturn(Optional.of(product));

        assertThrows(
                ProductOrderAccessDeniedException.class,
                () -> orderService.create(
                        "seller@example.com",
                        createOrderRequest()
                )
        );

        verify(productRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancelShouldRestoreStock() {
        User buyer = createBuyer();
        User seller = createSeller();
        Product product = createProduct(seller);

        product.setStockQuantity(22);

        ProductOrder order =
                createOrder(product, buyer);
        order.setStatus(OrderStatus.PENDING);

        when(userRepository.findByEmailIgnoreCase(
                "buyer@example.com"
        )).thenReturn(Optional.of(buyer));

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        ProductOrderResponse response =
                orderService.cancel(
                        "buyer@example.com",
                        100L
                );

        assertEquals(
                OrderStatus.CANCELLED,
                response.getStatus()
        );

        assertEquals(25, product.getStockQuantity());

        verify(productRepository).save(product);
        verify(orderRepository).save(order);
    }

    @Test
    void rejectShouldRestoreStock() {
        User buyer = createBuyer();
        User seller = createSeller();
        Product product = createProduct(seller);

        product.setStockQuantity(22);

        ProductOrder order =
                createOrder(product, buyer);
        order.setStatus(OrderStatus.PENDING);

        when(userRepository.findByEmailIgnoreCase(
                "seller@example.com"
        )).thenReturn(Optional.of(seller));

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        OrderStatusUpdateRequest request =
                new OrderStatusUpdateRequest();

        request.setStatus(OrderStatus.REJECTED);
        request.setSellerResponseNote(
                "Currently unavailable"
        );

        ProductOrderResponse response =
                orderService.updateStatus(
                        "seller@example.com",
                        100L,
                        request
                );

        assertEquals(
                OrderStatus.REJECTED,
                response.getStatus()
        );

        assertEquals(25, product.getStockQuantity());

        verify(productRepository).save(product);
        verify(orderRepository).save(order);
    }

    @Test
    void sellerShouldAcceptPendingOrder() {
        User buyer = createBuyer();
        User seller = createSeller();
        Product product = createProduct(seller);

        ProductOrder order =
                createOrder(product, buyer);
        order.setStatus(OrderStatus.PENDING);

        when(userRepository.findByEmailIgnoreCase(
                "seller@example.com"
        )).thenReturn(Optional.of(seller));

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        OrderStatusUpdateRequest request =
                new OrderStatusUpdateRequest();

        request.setStatus(OrderStatus.ACCEPTED);

        ProductOrderResponse response =
                orderService.updateStatus(
                        "seller@example.com",
                        100L,
                        request
                );

        assertEquals(
                OrderStatus.ACCEPTED,
                response.getStatus()
        );

        verify(productRepository, never()).save(any());
        verify(orderRepository).save(order);
    }

    @Test
    void acceptedOrderShouldBeCompleted() {
        User buyer = createBuyer();
        User seller = createSeller();
        Product product = createProduct(seller);

        ProductOrder order =
                createOrder(product, buyer);
        order.setStatus(OrderStatus.ACCEPTED);

        when(userRepository.findByEmailIgnoreCase(
                "seller@example.com"
        )).thenReturn(Optional.of(seller));

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        OrderStatusUpdateRequest request =
                new OrderStatusUpdateRequest();

        request.setStatus(OrderStatus.COMPLETED);

        ProductOrderResponse response =
                orderService.updateStatus(
                        "seller@example.com",
                        100L,
                        request
                );

        assertEquals(
                OrderStatus.COMPLETED,
                response.getStatus()
        );

        verify(orderRepository).save(order);
    }

    @Test
    void acceptedOrderShouldNotBeCancelled() {
        User buyer = createBuyer();
        User seller = createSeller();
        Product product = createProduct(seller);

        ProductOrder order =
                createOrder(product, buyer);
        order.setStatus(OrderStatus.ACCEPTED);

        when(userRepository.findByEmailIgnoreCase(
                "buyer@example.com"
        )).thenReturn(Optional.of(buyer));

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        assertThrows(
                InvalidOrderStatusException.class,
                () -> orderService.cancel(
                        "buyer@example.com",
                        100L
                )
        );

        verify(productRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    private User createBuyer() {
        User buyer = new User();
        buyer.setId(2L);
        buyer.setFullName("Test Buyer");
        buyer.setEmail("buyer@example.com");

        return buyer;
    }

    private User createSeller() {
        User seller = new User();
        seller.setId(1L);
        seller.setFullName("Test Seller");
        seller.setEmail("seller@example.com");

        return seller;
    }

    private Product createProduct(User seller) {
        Product product = new Product();
        product.setId(20L);
        product.setSeller(seller);
        product.setName("Organic Fertilizer");
        product.setCategory(
                ProductCategory.FERTILIZER
        );
        product.setPrice(
                new BigDecimal("750.00")
        );
        product.setStockQuantity(25);
        product.setUnit(ProductUnit.PACK);
        product.setServiceAddress("Main Road");
        product.setVillage("Chinnagundavelly");
        product.setDistrict("Siddipet");
        product.setState("Telangana");
        product.setPostalCode("502103");
        product.setActive(true);

        return product;
    }

    private ProductOrderRequest createOrderRequest() {
        ProductOrderRequest request =
                new ProductOrderRequest();

        request.setProductId(20L);
        request.setQuantity(3);
        request.setBuyerNote(
                "Please deliver carefully"
        );

        return request;
    }

    private ProductOrder createOrder(
            Product product,
            User buyer
    ) {
        ProductOrder order = new ProductOrder();
        order.setId(100L);
        order.setProduct(product);
        order.setBuyer(buyer);
        order.setQuantity(3);
        order.setUnitPrice(
                new BigDecimal("750.00")
        );
        order.setTotalAmount(
                new BigDecimal("2250.00")
        );
        order.setStatus(OrderStatus.PENDING);

        return order;
    }
}