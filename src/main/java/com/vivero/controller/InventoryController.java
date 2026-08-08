package com.vivero.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.dto.InventoryDTOs.*;
import com.vivero.service.InventoryService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Control de Entradas, Salidas, Ajustes y Reservas por m² y unidades")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/movements")
    @Operation(summary = "Obtener últimos movimientos de inventario")
    public ResponseEntity<List<InventoryMovementDTO>> getRecentMovements() {
        return ResponseEntity.ok(inventoryService.getRecentMovements());
    }

    @GetMapping("/movements/product/{productId}")
    @Operation(summary = "Obtener movimientos de un producto específico")
    public ResponseEntity<List<InventoryMovementDTO>> getMovementsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getMovementsByProduct(productId));
    }

    @PostMapping("/adjust")
    @Operation(summary = "Registrar ajuste/entrada/salida de stock")
    public ResponseEntity<InventoryMovementDTO> adjustStock(@Valid @RequestBody StockAdjustmentRequest request, Principal principal) {
        String username = principal != null ? principal.getName() : "admin";
        return ResponseEntity.ok(inventoryService.adjustStock(request, username));
    }
}
