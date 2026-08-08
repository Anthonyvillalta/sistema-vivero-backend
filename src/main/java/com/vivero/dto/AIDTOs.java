package com.vivero.dto;

import lombok.*;

public class AIDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnalyzeProductRequest {
        private String image;
        private String mimeType;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductAnalysisDTO {
        private String name;
        private String categoryName;
        private String description;
        private String imageUrl;
        private String message;
    }
}
