package com.vivero.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class UbigeoDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DepartmentDTO {
        private Long id;
        private String code;
        private String name;
        private Boolean active;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProvinceDTO {
        private Long id;
        private String code;
        private String name;
        private Long departmentId;
        private String departmentName;
        private Boolean active;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DistrictDTO {
        private Long id;
        private String code;
        private String name;
        private Long provinceId;
        private String provinceName;
        private Long departmentId;
        private String departmentName;
        private Boolean active;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDepartmentRequest {
        @NotBlank(message = "El código es obligatorio")
        private String code;

        @NotBlank(message = "El nombre es obligatorio")
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProvinceRequest {
        @NotBlank(message = "El código es obligatorio")
        private String code;

        @NotBlank(message = "El nombre es obligatorio")
        private String name;

        @NotNull(message = "El id del departamento es obligatorio")
        private Long departmentId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDistrictRequest {
        @NotBlank(message = "El código es obligatorio")
        private String code;

        @NotBlank(message = "El nombre es obligatorio")
        private String name;

        @NotNull(message = "El id de la provincia es obligatorio")
        private Long provinceId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDepartmentRequest {
        @NotBlank(message = "El código es obligatorio")
        private String code;

        @NotBlank(message = "El nombre es obligatorio")
        private String name;

        private Boolean active;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProvinceRequest {
        @NotBlank(message = "El código es obligatorio")
        private String code;

        @NotBlank(message = "El nombre es obligatorio")
        private String name;

        @NotNull(message = "El id del departamento es obligatorio")
        private Long departmentId;

        private Boolean active;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDistrictRequest {
        @NotBlank(message = "El código es obligatorio")
        private String code;

        @NotBlank(message = "El nombre es obligatorio")
        private String name;

        @NotNull(message = "El id de la provincia es obligatorio")
        private Long provinceId;

        private Boolean active;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BulkImportItem {
        private String departmentCode;
        private String departmentName;
        private String provinceCode;
        private String provinceName;
        private String districtCode;
        private String districtName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkUbigeoImportRequest {
        @NotNull(message = "La lista de registros no puede estar vacía")
        private java.util.List<BulkImportItem> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BulkImportResponse {
        private int importedDepartments;
        private int importedProvinces;
        private int importedDistricts;
        private int totalRecords;
        private String message;
    }
}
