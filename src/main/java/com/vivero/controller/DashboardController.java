package com.vivero.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.dto.DashboardDTOs.DashboardMetricsDTO;
import com.vivero.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard Executive", description = "Resumen de Métricas de Ventas, Gastos, Stock Crítico y Pedidos")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/metrics")
    @Operation(summary = "Obtener todas las métricas ejecutivas para el Dashboard")
    public ResponseEntity<DashboardMetricsDTO> getMetrics() {
        return ResponseEntity.ok(dashboardService.getExecutiveMetrics());
    }
}
