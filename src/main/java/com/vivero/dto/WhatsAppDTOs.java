package com.vivero.dto;

import lombok.*;

public class WhatsAppDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WhatsAppMessageDTO {
        private String phone;
        private String customerName;
        private String message;
        private String waLink;
    }
}
