package com.vivero.dto;

import lombok.*;

import java.math.BigDecimal;

public class SettingsDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompanySettingsDTO {
        private String companyName;
        private String companyRuc;
        private String companyPhone;
        private String companyAddress;
        private BigDecimal warehouseLatitude;
        private BigDecimal warehouseLongitude;
        private String geminiApiKey;
    }
}
