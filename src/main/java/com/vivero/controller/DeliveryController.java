package com.vivero.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.dto.DeliveryDTOs.*;
import com.vivero.dto.OrderDTOs.DeliveryDTO;
import com.vivero.service.DeliveryService;

import java.util.List;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
@Tag(name = "Control de Delivery & Rutas GPS", description = "Posiciones GPS de repartidores, destinos y ETA persistidos en MySQL")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    @Operation(summary = "Listar todas las rutas GPS de delivery (base de datos MySQL)")
    public ResponseEntity<List<DeliveryDTO>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Obtener ruta GPS de un pedido específico")
    public ResponseEntity<DeliveryDTO> getDeliveryByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.getDeliveryByOrder(orderId));
    }

    @PutMapping("/order/{orderId}/gps")
    @Operation(summary = "Guardar posición GPS actual del repartidor en MySQL")
    public ResponseEntity<DeliveryDTO> updateGpsPosition(@PathVariable Long orderId,
                                                         @RequestBody UpdateGpsPositionRequest request) {
        return ResponseEntity.ok(deliveryService.updateGpsPosition(orderId, request));
    }

    @PutMapping("/order/{orderId}/destination")
    @Operation(summary = "Guardar coordenadas del destino del cliente en MySQL")
    public ResponseEntity<DeliveryDTO> updateDestination(@PathVariable Long orderId,
                                                         @RequestBody UpdateDestinationRequest request) {
        return ResponseEntity.ok(deliveryService.updateDestination(orderId, request));
    }

    @PutMapping("/order/{orderId}/eta")
    @Operation(summary = "Guardar hora estimada de llegada (ETA) en MySQL")
    public ResponseEntity<DeliveryDTO> updateEta(@PathVariable Long orderId,
                                                 @RequestBody UpdateEtaRequest request) {
        return ResponseEntity.ok(deliveryService.updateEta(orderId, request));
    }
}
