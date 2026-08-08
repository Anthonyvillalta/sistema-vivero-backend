package com.vivero.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.dto.SupplierDTOs.*;
import com.vivero.service.SupplierService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
@Tag(name = "Compras", description = "Gestión de Compras a Proveedores y Actualización Automática de Inventario")
public class PurchaseController {

    private final SupplierService supplierService;

    @GetMapping
    @Operation(summary = "Listar compras recientes")
    public ResponseEntity<List<PurchaseDTO>> getRecentPurchases() {
        return ResponseEntity.ok(supplierService.getRecentPurchases());
    }

    @PostMapping
    @Operation(summary = "Registrar nueva compra y actualizar inventario")
    public ResponseEntity<PurchaseDTO> createPurchase(@Valid @RequestBody CreatePurchaseRequest request, Principal principal) {
        String username = principal != null ? principal.getName() : "admin";
        return ResponseEntity.ok(supplierService.createPurchase(request, username));
    }
}
