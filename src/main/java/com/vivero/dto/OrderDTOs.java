package com.vivero.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.vivero.entity.OrderStatus;

public class OrderDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDTO {
        private Long id;
        private String orderNumber;
        private Long saleId;
        private Long customerId;
        private String customerName;
        private String customerPhone;
        private String deliveryAddress;
        private LocalDateTime deliveryDate;
        private String deliveryTimeSlot;
        private OrderStatus status;
        private String assignedDriverName;
        private String assignedDriverPhone;
        private String deliveryNotes;
        private String productsSummary;
        private LocalDateTime createdAt;
        private DeliveryDTO delivery;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeliveryDTO {
        private Long id;
        private Long orderId;
        private String orderNumber;
        private String customerName;
        private String deliveryAddress;
        private String driverName;
        private String driverPhone;
        private String routeStatus;
        private BigDecimal currentLatitude;
        private BigDecimal currentLongitude;
        private BigDecimal destinationLatitude;
        private BigDecimal destinationLongitude;
        private BigDecimal gpsAccuracy;
        private BigDecimal gpsSpeed;
        private LocalDateTime estimatedArrival;
        private LocalDateTime deliveredAt;
        private String recipientNotes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateOrderStatusRequest {
        private OrderStatus status;
        private String driverName;
        private String driverPhone;
        private String notes;
    }
}
