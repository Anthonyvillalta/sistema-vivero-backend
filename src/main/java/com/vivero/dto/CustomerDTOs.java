package com.vivero.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerDTO {
        private Long id;
        private String fullName;
        private String documentType;
        private String documentNumber;
        private String phone;
        private String whatsapp;
        private String email;
        private String address;
        private Boolean isFrequent;
        private BigDecimal totalPurchases;
        private LocalDateTime lastPurchaseDate;
        private String notes;
        private Boolean active;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCustomerRequest {
        @NotBlank(message = "El nombre completo es requerido")
        private String fullName;

        private String documentType;
        private String documentNumber;

        @NotBlank(message = "El teléfono es requerido")
        private String phone;

        private String whatsapp;
        private String email;
        private String address;
        private String notes;
    }
}
