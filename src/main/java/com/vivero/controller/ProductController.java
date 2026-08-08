package com.vivero.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.dto.ProductDTOs.*;
import com.vivero.service.ProductService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión de Grass (m²) y Plantas (unidades)")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Listar todos los productos activos")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Listar productos por categoría")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de producto por ID")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/critical-stock")
    @Operation(summary = "Obtener productos con stock crítico o bajo")
    public ResponseEntity<List<ProductDTO>> getCriticalStockProducts() {
        return ResponseEntity.ok(productService.getCriticalStockProducts());
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo producto")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request, Principal principal) {
        String username = principal != null ? principal.getName() : "admin";
        return ResponseEntity.ok(productService.createProduct(request, username));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto existente")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (desactivar) producto")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
