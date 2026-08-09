package com.vivero.controller;

import com.vivero.dto.ReportDTOs.*;
import com.vivero.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reportes Gerenciales", description = "Reportes financieros, de ventas, inventario y analytics para la toma de decisiones")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales-summary")
    @Operation(summary = "Resumen de ventas por rango de fechas")
    public ResponseEntity<SalesSummaryDTO> getSalesSummary(
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha de fin (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getSalesSummary(startDate, endDate));
    }

    @GetMapping("/sales-by-product")
    @Operation(summary = "Ranking de productos más vendidos")
    public ResponseEntity<List<ProductSalesDTO>> getProductSalesRanking(
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha de fin (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Límite de resultados")
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(reportService.getProductSalesRanking(startDate, endDate, limit));
    }

    @GetMapping("/expenses-summary")
    @Operation(summary = "Resumen de gastos por rango de fechas")
    public ResponseEntity<ExpenseSummaryDTO> getExpenseSummary(
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha de fin (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getExpenseSummary(startDate, endDate));
    }

    @GetMapping("/purchases-summary")
    @Operation(summary = "Resumen de compras por rango de fechas")
    public ResponseEntity<PurchaseSummaryDTO> getPurchaseSummary(
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha de fin (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getPurchaseSummary(startDate, endDate));
    }

    @GetMapping("/profit-margins")
    @Operation(summary = "Análisis de márgenes de ganancia")
    public ResponseEntity<ProfitMarginDTO> getProfitMargin(
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha de fin (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getProfitMargin(startDate, endDate));
    }

    @GetMapping("/inventory-valuation")
    @Operation(summary = "Valoración del inventario actual")
    public ResponseEntity<InventoryValuationDTO> getInventoryValuation() {
        return ResponseEntity.ok(reportService.getInventoryValuation());
    }

    @GetMapping("/top-customers")
    @Operation(summary = "Clientes con mayor valor de compra")
    public ResponseEntity<List<TopCustomerDTO>> getTopCustomers(
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha de fin (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Límite de resultados")
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(reportService.getTopCustomers(startDate, endDate, limit));
    }
}
