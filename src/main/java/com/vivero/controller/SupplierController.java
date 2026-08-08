package com.vivero.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.dto.SupplierDTOs.*;
import com.vivero.service.SupplierService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "Gestión de Proveedores")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    @Operation(summary = "Listar proveedores")
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo proveedor")
    public ResponseEntity<SupplierDTO> createSupplier(@RequestBody CreateSupplierRequest request, Principal principal) {
        String username = principal != null ? principal.getName() : "admin";
        return ResponseEntity.ok(supplierService.createSupplier(request, username));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un proveedor")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable Long id, @RequestBody CreateSupplierRequest request) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar / Desactivar un proveedor")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
