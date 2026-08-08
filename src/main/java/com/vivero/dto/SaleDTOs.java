package com.vivero.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.vivero.entity.UnitType;

public class SaleDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaleDTO {
        private Long id;
        private String receiptNumber;
        private Long customerId;
        private String customerName;
        private String customerPhone;
        private LocalDateTime saleDate;
        private BigDecimal subtotal;
        private BigDecimal deliveryFee;
        private BigDecimal discount;
        private BigDecimal total;
        private String paymentMethod;
        private String paymentStatus;
        private String sellerUsername;
        private List<SaleItemDTO> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaleItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private UnitType unitType;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateSaleRequest {
        private Long customerId;
        private String customerName;
        private String customerPhone;

        @NotEmpty(message = "La venta debe contener al menos un producto")
        private List<CreateSaleItemRequest> items;

        private BigDecimal deliveryFee;
        private BigDecimal discount;

        @NotNull(message = "El método de pago es requerido")
        private String paymentMethod; // EFECTIVO, YAPE, PLIN, TRANSFERENCIA

        private Boolean createOrderForDelivery;
        private String deliveryAddress;
        private String deliveryTimeSlot;
        private String deliveryDate; // yyyy-MM-dd
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateSaleItemRequest {
        @NotNull(message = "El ID de producto es requerido")
        private Long productId;

        @NotNull(message = "La cantidad es requerida")
        private BigDecimal quantity;

        @NotNull(message = "El precio unitario es requerido")
        private BigDecimal unitPrice;
    }
}
