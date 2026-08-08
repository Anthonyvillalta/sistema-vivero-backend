package com.vivero.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

public class DeliveryMethodDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeliveryMethodDTO {
        private Long id;
        private String name;
        private String type; // "DELIVERY" or "STORE"
        private BigDecimal price;
        private String estimatedTime;
        private String description;
        private Boolean active;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDeliveryMethodRequest {
        @NotBlank(message = "El nombre del método de entrega es obligatorio")
        private String name;

        private String type; // "DELIVERY" or "STORE"

        @NotNull(message = "El costo/tarifa es obligatorio")
        private BigDecimal price;

        private String estimatedTime;
        private String description;
        private Boolean active;
    }
}
