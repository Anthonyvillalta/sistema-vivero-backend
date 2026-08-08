package com.vivero.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.dto.DeliveryMethodDTOs.*;
import com.vivero.service.DeliveryMethodService;

import java.util.List;

@RestController
@RequestMapping("/delivery-methods")
@RequiredArgsConstructor
@Tag(name = "Métodos de Entrega", description = "Gestión de Opciones de Delivery y Recojo en Tienda")
public class DeliveryMethodController {

    private final DeliveryMethodService deliveryMethodService;

    @GetMapping
    @Operation(summary = "Listar todos los métodos de entrega")
    public ResponseEntity<List<DeliveryMethodDTO>> getAllDeliveryMethods() {
        return ResponseEntity.ok(deliveryMethodService.getAllDeliveryMethods());
    }

    @GetMapping("/active")
    @Operation(summary = "Listar métodos de entrega activos para el carrito de compras")
    public ResponseEntity<List<DeliveryMethodDTO>> getActiveDeliveryMethods() {
        return ResponseEntity.ok(deliveryMethodService.getActiveDeliveryMethods());
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo método de entrega")
    public ResponseEntity<DeliveryMethodDTO> createDeliveryMethod(@Valid @RequestBody CreateDeliveryMethodRequest request) {
        return ResponseEntity.ok(deliveryMethodService.createDeliveryMethod(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un método de entrega")
    public ResponseEntity<DeliveryMethodDTO> updateDeliveryMethod(@PathVariable Long id, @Valid @RequestBody CreateDeliveryMethodRequest request) {
        return ResponseEntity.ok(deliveryMethodService.updateDeliveryMethod(id, request));
    }

    @PutMapping("/{id}/toggle-status")
    @Operation(summary = "Cambiar estado activo/inactivo de un método de entrega")
    public ResponseEntity<DeliveryMethodDTO> toggleDeliveryMethodStatus(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryMethodService.toggleDeliveryMethodStatus(id));
    }
}
