package com.agriseva.product.service;

import com.agriseva.product.dto.ProductRequest;
import com.agriseva.product.dto.ProductResponse;
import com.agriseva.product.exception.ProductAccessDeniedException;
import com.agriseva.product.exception.ProductSellerRoleRequiredException;
import com.agriseva.product.model.Product;
import com.agriseva.product.model.ProductCategory;
import com.agriseva.product.model.ProductUnit;
import com.agriseva.product.repository.ProductRepository;
import com.agriseva.user.model.Role;
import com.agriseva.user.model.RoleType;
import com.agriseva.user.model.User;
import com.agriseva.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(
                productRepository,
                userRepository
        );
    }

    @Test
    void createShouldSaveProductForSeller() {
        User seller = createSeller();
        ProductRequest request = createRequest();

        when(userRepository.findByEmailIgnoreCase(
                "sharath.test@example.com"
        )).thenReturn(Optional.of(seller));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.setId(20L);
                    return product;
                });

        ProductResponse response = productService.create(
                "sharath.test@example.com",
                request
        );

        assertEquals(20L, response.getId());
        assertEquals(1L, response.getSellerId());
        assertEquals("Organic Fertilizer", response.getName());
        assertEquals(
                ProductCategory.FERTILIZER,
                response.getCategory()
        );
        assertEquals(
                new BigDecimal("750.00"),
                response.getPrice()
        );
        assertEquals(25, response.getStockQuantity());
        assertEquals(ProductUnit.PACK, response.getUnit());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createShouldRejectUserWithoutProductSellerRole() {
        User user = new User();
        user.setId(1L);
        user.setEmail("farmer@example.com");
        user.addRole(new Role(RoleType.FARMER));

        when(userRepository.findByEmailIgnoreCase(
                "farmer@example.com"
        )).thenReturn(Optional.of(user));

        assertThrows(
                ProductSellerRoleRequiredException.class,
                () -> productService.create(
                        "farmer@example.com",
                        createRequest()
                )
        );

        verifyNoInteractions(productRepository);
    }

    @Test
    void updateShouldRejectAnotherSeller() {
        User authenticatedSeller = createSeller();

        User actualSeller = new User();
        actualSeller.setId(2L);
        actualSeller.setFullName("Another Seller");

        Product product = createProduct(actualSeller);
        product.setId(20L);

        when(userRepository.findByEmailIgnoreCase(
                "sharath.test@example.com"
        )).thenReturn(Optional.of(authenticatedSeller));

        when(productRepository.findById(20L))
                .thenReturn(Optional.of(product));

        assertThrows(
                ProductAccessDeniedException.class,
                () -> productService.update(
                        "sharath.test@example.com",
                        20L,
                        createRequest()
                )
        );

        verify(productRepository, never()).save(any());
    }

    @Test
    void deactivateShouldMakeProductInactive() {
        User seller = createSeller();

        Product product = createProduct(seller);
        product.setId(20L);

        when(userRepository.findByEmailIgnoreCase(
                "sharath.test@example.com"
        )).thenReturn(Optional.of(seller));

        when(productRepository.findById(20L))
                .thenReturn(Optional.of(product));

        productService.deactivate(
                "sharath.test@example.com",
                20L
        );

        assertFalse(product.isActive());

        verify(productRepository).save(product);
    }

    @Test
    void getAllActiveProductsShouldReturnMappedProducts() {
        User seller = createSeller();

        Product product = createProduct(seller);
        product.setId(20L);

        when(productRepository
                .findByActiveTrueOrderByCreatedAtDesc())
                .thenReturn(List.of(product));

        List<ProductResponse> responses =
                productService.getAllActiveProducts();

        assertEquals(1, responses.size());
        assertEquals(
                "Organic Fertilizer",
                responses.get(0).getName()
        );
        assertEquals(
                ProductCategory.FERTILIZER,
                responses.get(0).getCategory()
        );
    }

    private User createSeller() {
        User seller = new User();
        seller.setId(1L);
        seller.setFullName("Sharath Reddy");
        seller.setEmail("sharath.test@example.com");
        seller.addRole(new Role(RoleType.FARMER));
        seller.addRole(new Role(RoleType.PRODUCT_SELLER));

        return seller;
    }

    private Product createProduct(User seller) {
        Product product = new Product();
        product.setSeller(seller);
        product.setName("Organic Fertilizer");
        product.setCategory(ProductCategory.FERTILIZER);
        product.setDescription("Organic fertilizer for crops");
        product.setPrice(new BigDecimal("750.00"));
        product.setStockQuantity(25);
        product.setUnit(ProductUnit.PACK);
        product.setImageUrl(
                "https://example.com/fertilizer.jpg"
        );
        product.setServiceAddress("Main Road");
        product.setVillage("Chinnagundavelly");
        product.setDistrict("Siddipet");
        product.setState("Telangana");
        product.setPostalCode("502103");
        product.setActive(true);

        return product;
    }

    private ProductRequest createRequest() {
        ProductRequest request = new ProductRequest();
        request.setName("Organic Fertilizer");
        request.setCategory(ProductCategory.FERTILIZER);
        request.setDescription(
                "Organic fertilizer for crops"
        );
        request.setPrice(new BigDecimal("750.00"));
        request.setStockQuantity(25);
        request.setUnit(ProductUnit.PACK);
        request.setImageUrl(
                "https://example.com/fertilizer.jpg"
        );
        request.setServiceAddress("Main Road");
        request.setVillage("Chinnagundavelly");
        request.setDistrict("Siddipet");
        request.setState("Telangana");
        request.setPostalCode("502103");

        return request;
    }
}