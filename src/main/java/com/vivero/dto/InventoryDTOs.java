package com.vivero.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.vivero.entity.MovementType;

public class InventoryDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InventoryMovementDTO {
        private Long id;
        private Long productId;
        private String productName;
        private String unitType;
        private MovementType movementType;
        private BigDecimal quantity;
        private BigDecimal previousStock;
        private BigDecimal newStock;
        private String reason;
        private String referenceId;
        private LocalDateTime createdAt;
        private String createdBy;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockAdjustmentRequest {
        @NotNull(message = "El ID de producto es requerido")
        private Long productId;

        @NotNull(message = "El tipo de movimiento es requerido")
        private MovementType movementType; // ENTRADA, SALIDA, AJUSTE

        @NotNull(message = "La cantidad es requerida")
        private BigDecimal quantity;

        private String reason;
    }
}
