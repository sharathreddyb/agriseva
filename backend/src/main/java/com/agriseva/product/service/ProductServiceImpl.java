package com.agriseva.product.service;

import com.agriseva.product.dto.ProductRequest;
import com.agriseva.product.dto.ProductResponse;
import com.agriseva.product.exception.ProductAccessDeniedException;
import com.agriseva.product.exception.ProductNotFoundException;
import com.agriseva.product.exception.ProductSellerRoleRequiredException;
import com.agriseva.product.model.Product;
import com.agriseva.product.repository.ProductRepository;
import com.agriseva.user.model.RoleType;
import com.agriseva.user.model.User;
import com.agriseva.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProductResponse create(
            String authenticatedEmail,
            ProductRequest request
    ) {
        User seller =
                getAuthenticatedUser(authenticatedEmail);

        verifyProductSellerRole(seller);

        Product product = new Product();
        product.setSeller(seller);

        updateProductFields(product, request);

        Product savedProduct =
                productRepository.save(product);

        return buildResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse update(
            String authenticatedEmail,
            Long productId,
            ProductRequest request
    ) {
        User seller =
                getAuthenticatedUser(authenticatedEmail);

        verifyProductSellerRole(seller);

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(productId)
                );

        verifyOwnership(product, seller);

        updateProductFields(product, request);

        Product savedProduct =
                productRepository.save(product);

        return buildResponse(savedProduct);
    }

    @Override
    @Transactional
    public void deactivate(
            String authenticatedEmail,
            Long productId
    ) {
        User seller =
                getAuthenticatedUser(authenticatedEmail);

        verifyProductSellerRole(seller);

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(productId)
                );

        verifyOwnership(product, seller);

        product.setActive(false);

        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long productId) {
        Product product = productRepository
                .findByIdAndActiveTrue(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(productId)
                );

        return buildResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllActiveProducts() {
        return productRepository
                .findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getMyProducts(
            String authenticatedEmail
    ) {
        User seller =
                getAuthenticatedUser(authenticatedEmail);

        return productRepository
                .findBySellerIdOrderByCreatedAtDesc(
                        seller.getId()
                )
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    private User getAuthenticatedUser(
            String authenticatedEmail
    ) {
        return userRepository
                .findByEmailIgnoreCase(authenticatedEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user was not found"
                        )
                );
    }

    private void verifyProductSellerRole(User user) {
        boolean hasProductSellerRole = user.getRoles()
                .stream()
                .anyMatch(role ->
                        role.getName()
                                == RoleType.PRODUCT_SELLER
                );

        if (!hasProductSellerRole) {
            throw new ProductSellerRoleRequiredException();
        }
    }

    private void verifyOwnership(
            Product product,
            User authenticatedUser
    ) {
        if (!product.getSeller()
                .getId()
                .equals(authenticatedUser.getId())) {
            throw new ProductAccessDeniedException();
        }
    }

    private void updateProductFields(
            Product product,
            ProductRequest request
    ) {
        product.setName(
                request.getName().trim()
        );

        product.setCategory(
                request.getCategory()
        );

        product.setDescription(
                trimToNull(request.getDescription())
        );

        product.setPrice(
                request.getPrice()
        );

        product.setStockQuantity(
                request.getStockQuantity()
        );

        product.setUnit(
                request.getUnit()
        );

        product.setImageUrl(
                trimToNull(request.getImageUrl())
        );

        product.setServiceAddress(
                request.getServiceAddress().trim()
        );

        product.setVillage(
                request.getVillage().trim()
        );

        product.setDistrict(
                request.getDistrict().trim()
        );

        product.setState(
                request.getState().trim()
        );

        product.setPostalCode(
                request.getPostalCode().trim()
        );
    }

    private ProductResponse buildResponse(
            Product product
    ) {
        return ProductResponse.builder()
                .id(product.getId())
                .sellerId(
                        product.getSeller().getId()
                )
                .sellerName(
                        product.getSeller().getFullName()
                )
                .name(product.getName())
                .category(product.getCategory())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(
                        product.getStockQuantity()
                )
                .unit(product.getUnit())
                .imageUrl(product.getImageUrl())
                .serviceAddress(
                        product.getServiceAddress()
                )
                .village(product.getVillage())
                .district(product.getDistrict())
                .state(product.getState())
                .postalCode(product.getPostalCode())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}