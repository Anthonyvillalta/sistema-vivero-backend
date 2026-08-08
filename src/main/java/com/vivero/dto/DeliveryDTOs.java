package com.vivero.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DeliveryDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateGpsPositionRequest {
        private BigDecimal latitude;
        private BigDecimal longitude;
        private BigDecimal accuracy;
        private BigDecimal speed;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateDestinationRequest {
        private BigDecimal latitude;
        private BigDecimal longitude;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateEtaRequest {
        private LocalDateTime estimatedArrival;
    }
}
