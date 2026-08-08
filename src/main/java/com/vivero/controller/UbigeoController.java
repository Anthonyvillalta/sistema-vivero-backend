package com.vivero.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.dto.UbigeoDTOs.*;
import com.vivero.service.UbigeoService;

import java.util.List;

@RestController
@RequestMapping("/ubigeo")
@RequiredArgsConstructor
@Tag(name = "Ubigeo / Territorio", description = "Gestión de Departamentos, Provincias y Distritos de Perú")
public class UbigeoController {

    private final UbigeoService ubigeoService;

    @GetMapping("/departments")
    @Operation(summary = "Listar todos los departamentos")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        return ResponseEntity.ok(ubigeoService.getAllDepartments());
    }

    @PostMapping("/departments")
    @Operation(summary = "Crear un nuevo departamento")
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody CreateDepartmentRequest req) {
        return ResponseEntity.ok(ubigeoService.createDepartment(req));
    }

    @PutMapping("/departments/{id}")
    @Operation(summary = "Actualizar un departamento")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long id, @Valid @RequestBody UpdateDepartmentRequest req) {
        return ResponseEntity.ok(ubigeoService.updateDepartment(id, req));
    }

    @PutMapping("/departments/{id}/toggle-status")
    @Operation(summary = "Cambiar estado activo/inactivo de un departamento")
    public ResponseEntity<DepartmentDTO> toggleDepartmentStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ubigeoService.toggleDepartmentStatus(id));
    }

    @DeleteMapping("/departments/{id}")
    @Operation(summary = "Desactivar un departamento")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        ubigeoService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/provinces")
    @Operation(summary = "Listar todas las provincias")
    public ResponseEntity<List<ProvinceDTO>> getAllProvinces() {
        return ResponseEntity.ok(ubigeoService.getAllProvinces());
    }

    @GetMapping("/departments/{departmentId}/provinces")
    @Operation(summary = "Listar provincias por departamento")
    public ResponseEntity<List<ProvinceDTO>> getProvincesByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(ubigeoService.getProvincesByDepartment(departmentId));
    }

    @PostMapping("/provinces")
    @Operation(summary = "Crear una nueva provincia")
    public ResponseEntity<ProvinceDTO> createProvince(@Valid @RequestBody CreateProvinceRequest req) {
        return ResponseEntity.ok(ubigeoService.createProvince(req));
    }

    @PutMapping("/provinces/{id}")
    @Operation(summary = "Actualizar una provincia")
    public ResponseEntity<ProvinceDTO> updateProvince(@PathVariable Long id, @Valid @RequestBody UpdateProvinceRequest req) {
        return ResponseEntity.ok(ubigeoService.updateProvince(id, req));
    }

    @PutMapping("/provinces/{id}/toggle-status")
    @Operation(summary = "Cambiar estado activo/inactivo de una provincia")
    public ResponseEntity<ProvinceDTO> toggleProvinceStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ubigeoService.toggleProvinceStatus(id));
    }

    @DeleteMapping("/provinces/{id}")
    @Operation(summary = "Desactivar una provincia")
    public ResponseEntity<Void> deleteProvince(@PathVariable Long id) {
        ubigeoService.deleteProvince(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/districts")
    @Operation(summary = "Listar todos los distritos")
    public ResponseEntity<List<DistrictDTO>> getAllDistricts() {
        return ResponseEntity.ok(ubigeoService.getAllDistricts());
    }

    @GetMapping("/provinces/{provinceId}/districts")
    @Operation(summary = "Listar distritos por provincia")
    public ResponseEntity<List<DistrictDTO>> getDistrictsByProvince(@PathVariable Long provinceId) {
        return ResponseEntity.ok(ubigeoService.getDistrictsByProvince(provinceId));
    }

    @PostMapping("/districts")
    @Operation(summary = "Crear un nuevo distrito")
    public ResponseEntity<DistrictDTO> createDistrict(@Valid @RequestBody CreateDistrictRequest req) {
        return ResponseEntity.ok(ubigeoService.createDistrict(req));
    }

    @PutMapping("/districts/{id}")
    @Operation(summary = "Actualizar un distrito")
    public ResponseEntity<DistrictDTO> updateDistrict(@PathVariable Long id, @Valid @RequestBody UpdateDistrictRequest req) {
        return ResponseEntity.ok(ubigeoService.updateDistrict(id, req));
    }

    @PutMapping("/districts/{id}/toggle-status")
    @Operation(summary = "Cambiar estado activo/inactivo de un distrito")
    public ResponseEntity<DistrictDTO> toggleDistrictStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ubigeoService.toggleDistrictStatus(id));
    }

    @DeleteMapping("/districts/{id}")
    @Operation(summary = "Desactivar un distrito")
    public ResponseEntity<Void> deleteDistrict(@PathVariable Long id) {
        ubigeoService.deleteDistrict(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk-import")
    @Operation(summary = "Importación masiva de Ubigeo desde Excel (Departamentos, Provincias y Distritos)")
    public ResponseEntity<BulkImportResponse> importBulkUbigeo(@Valid @RequestBody BulkUbigeoImportRequest req) {
        return ResponseEntity.ok(ubigeoService.importBulkUbigeo(req));
    }
}
