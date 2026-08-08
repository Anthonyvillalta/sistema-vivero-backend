package com.vivero.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.dto.DriverDTOs.*;
import com.vivero.service.DriverService;

import java.util.List;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
@Tag(name = "Repartidores", description = "Gestión de Conductores y Personal de Delivery")
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    @Operation(summary = "Listar todos los repartidores")
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo repartidor")
    public ResponseEntity<DriverDTO> createDriver(@Valid @RequestBody CreateDriverRequest request) {
        return ResponseEntity.ok(driverService.createDriver(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un repartidor")
    public ResponseEntity<DriverDTO> updateDriver(@PathVariable Long id, @Valid @RequestBody CreateDriverRequest request) {
        return ResponseEntity.ok(driverService.updateDriver(id, request));
    }

    @PutMapping("/{id}/toggle-status")
    @Operation(summary = "Cambiar estado activo/inactivo de un repartidor")
    public ResponseEntity<DriverDTO> toggleDriverStatus(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.toggleDriverStatus(id));
    }
}
