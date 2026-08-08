package com.vivero.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

import com.vivero.entity.UnitType;

public class ProductDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductDTO {
        private Long id;
        private String code;
        private String name;
        private String variety;
        private String brand;
        private String description;
        private Long categoryId;
        private String categoryName;
        private UnitType unitType;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private Integer discountPercentage;
        private BigDecimal costPrice;
        private BigDecimal stock;
        private BigDecimal reservedStock;
        private BigDecimal availableStock;
        private BigDecimal minStock;
        private String imageUrl;
        private Boolean active;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProductRequest {
        private String code;

        @NotBlank(message = "El nombre del producto es requerido")
        private String name;

        private String variety;
        private String brand;
        private String description;

        @NotNull(message = "La categoría es requerida")
        private Long categoryId;

        @NotNull(message = "El tipo de unidad es requerido")
        private UnitType unitType;

        @NotNull(message = "El precio es requerido")
        @Min(value = 0, message = "El precio debe ser positivo")
        private BigDecimal price;

        private BigDecimal originalPrice;
        private Integer discountPercentage;

        private BigDecimal costPrice;
        private BigDecimal stock;
        private BigDecimal minStock;
        private String imageUrl;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryDTO {
        private Long id;
        private String name;
        private String description;
        private String iconName;
        private Boolean active;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCategoryRequest {
        @NotBlank(message = "El nombre de la categoría es requerido")
        private String name;
        private String description;
        private String iconName;
    }
}
