package com.vivero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivero.dto.ProductDTOs.*;
import com.vivero.entity.Category;
import com.vivero.entity.Product;
import com.vivero.exception.ResourceNotFoundException;
import com.vivero.repository.CategoryRepository;
import com.vivero.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return mapToDTO(product);
    }

    public List<ProductDTO> getCriticalStockProducts() {
        return productRepository.findCriticalStockProducts().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private Category findCategory(Long categoryId) {
        if (categoryId != null) {
            var existing = categoryRepository.findById(categoryId);
            if (existing.isPresent()) return existing.get();
        }
        return categoryRepository.findAll().stream().findFirst()
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Grass Natural")
                        .description("Categoría principal")
                        .iconName("Sprout")
                        .active(true)
                        .build()));
    }

    @Transactional
    public ProductDTO createProduct(CreateProductRequest request, String createdBy) {
        Category category = findCategory(request.getCategoryId());

        Product product = Product.builder()
                .code(request.getCode() != null ? request.getCode() : "PROD-" + System.currentTimeMillis())
                .name(request.getName())
                .variety(request.getVariety())
                .brand(request.getBrand())
                .description(request.getDescription())
                .category(category)
                .unitType(request.getUnitType())
                .price(request.getPrice())
                .originalPrice(request.getOriginalPrice())
                .discountPercentage(request.getDiscountPercentage())
                .costPrice(request.getCostPrice() != null ? request.getCostPrice() : BigDecimal.ZERO)
                .stock(request.getStock() != null ? request.getStock() : BigDecimal.ZERO)
                .reservedStock(BigDecimal.ZERO)
                .minStock(request.getMinStock() != null ? request.getMinStock() : new BigDecimal("10.00"))
                .imageUrl(request.getImageUrl())
                .active(true)
                .createdBy(createdBy)
                .build();

        productRepository.save(product);
        return mapToDTO(product);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, CreateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        if (request.getCategoryId() != null) {
            Category category = findCategory(request.getCategoryId());
            if (category != null) {
                product.setCategory(category);
            }
        }

        if (request.getCode() != null && !request.getCode().trim().isEmpty()) {
            product.setCode(request.getCode().trim());
        }
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            product.setName(request.getName().trim());
        }
        if (request.getVariety() != null) product.setVariety(request.getVariety());
        if (request.getBrand() != null) product.setBrand(request.getBrand());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getUnitType() != null) product.setUnitType(request.getUnitType());
        if (request.getPrice() != null) product.setPrice(request.getPrice());

        product.setOriginalPrice(request.getOriginalPrice());
        product.setDiscountPercentage(request.getDiscountPercentage());

        if (request.getCostPrice() != null) product.setCostPrice(request.getCostPrice());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getMinStock() != null) product.setMinStock(request.getMinStock());
        if (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty()) {
            product.setImageUrl(request.getImageUrl());
        }

        productRepository.save(product);
        return mapToDTO(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    public ProductDTO mapToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .variety(product.getVariety())
                .brand(product.getBrand())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .unitType(product.getUnitType())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .discountPercentage(product.getDiscountPercentage())
                .costPrice(product.getCostPrice())
                .stock(product.getStock())
                .reservedStock(product.getReservedStock())
                .availableStock(product.getAvailableStock())
                .minStock(product.getMinStock())
                .imageUrl(product.getImageUrl())
                .active(product.getActive())
                .build();
    }
}
