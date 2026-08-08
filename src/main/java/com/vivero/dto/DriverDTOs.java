package com.vivero.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class DriverDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DriverDTO {
        private Long id;
        private String fullName;
        private String documentNumber;
        private String phone;
        private String vehicleInfo;
        private String licenseNumber;
        private Boolean active;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDriverRequest {
        @NotBlank(message = "El nombre del repartidor es obligatorio")
        private String fullName;

        private String documentNumber;
        private String phone;
        private String vehicleInfo;
        private String licenseNumber;
        private Boolean active;
    }
}
