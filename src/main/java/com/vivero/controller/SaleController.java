package com.vivero.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.dto.SaleDTOs.*;
import com.vivero.service.SaleService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "Procesamiento de Ventas POS y Generación de Comprobantes Digitales")
public class SaleController {

    private final SaleService saleService;

    @GetMapping
    @Operation(summary = "Listar ventas recientes")
    public ResponseEntity<List<SaleDTO>> getRecentSales() {
        return ResponseEntity.ok(saleService.getRecentSales());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de venta por ID")
    public ResponseEntity<SaleDTO> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @PostMapping
    @Operation(summary = "Registrar nueva venta")
    public ResponseEntity<SaleDTO> createSale(@Valid @RequestBody CreateSaleRequest request, Principal principal) {
        String username = principal != null ? principal.getName() : "vendedor";
        return ResponseEntity.ok(saleService.createSale(request, username));
    }
}
