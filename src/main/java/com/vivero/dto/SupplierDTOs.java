package com.vivero.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SupplierDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SupplierDTO {
        private Long id;
        private String companyName;
        private String contactName;
        private String documentNumber;
        private String phone;
        private String email;
        private String address;
        private Boolean active;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateSupplierRequest {
        @NotBlank(message = "La razón social o nombre de la empresa es requerida")
        private String companyName;

        private String contactName;
        private String documentNumber;
        private String phone;
        private String email;
        private String address;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PurchaseDTO {
        private Long id;
        private String purchaseNumber;
        private Long supplierId;
        private String supplierName;
        private LocalDateTime purchaseDate;
        private BigDecimal totalAmount;
        private String status;
        private String notes;
        private List<PurchaseItemDTO> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PurchaseItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private BigDecimal quantity;
        private BigDecimal unitCost;
        private BigDecimal totalCost;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePurchaseRequest {
        private Long supplierId;
        private String supplierName;
        private List<CreatePurchaseItemRequest> items;
        private String notes;

        // Delivery Options
        private Boolean isDelivery;
        private String deliveryAddress;
        private String deliveryTimeSlot;
        private String deliveryDate; // yyyy-MM-dd
        private String deliveryNotes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePurchaseItemRequest {
        private Long productId;
        private BigDecimal quantity;
        private BigDecimal unitCost;
    }
}
