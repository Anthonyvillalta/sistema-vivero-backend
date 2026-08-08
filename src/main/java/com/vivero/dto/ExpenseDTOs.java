package com.vivero.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExpenseDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExpenseDTO {
        private Long id;
        private String category;
        private String description;
        private BigDecimal amount;
        private LocalDateTime expenseDate;
        private String paymentMethod;
        private String registeredBy;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateExpenseRequest {
        @NotBlank(message = "La categoría del gasto es requerida")
        private String category; // TRANSPORTE, PERSONAL, MANTENIMIENTO, INSUMOS, OTROS

        @NotBlank(message = "La descripción es requerida")
        private String description;

        @NotNull(message = "El monto es requerido")
        private BigDecimal amount;

        private String paymentMethod;
    }
}
