package com.vivero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivero.dto.DeliveryDTOs.*;
import com.vivero.dto.OrderDTOs.DeliveryDTO;
import com.vivero.entity.Delivery;
import com.vivero.entity.Order;
import com.vivero.entity.OrderStatus;
import com.vivero.exception.ResourceNotFoundException;
import com.vivero.repository.DeliveryRepository;
import com.vivero.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<DeliveryDTO> getAllDeliveries() {
        // Las entregas finalizadas no aparecen en el rastrero GPS activo.
        return deliveryRepository.findAll().stream()
                .filter(d -> !isDelivered(d))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeliveryDTO getDeliveryByOrder(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe registro de delivery para el pedido ID: " + orderId));
        return mapToDTO(delivery);
    }

    @Transactional
    public DeliveryDTO updateGpsPosition(Long orderId, UpdateGpsPositionRequest request) {
        Delivery delivery = getOrCreateForOrder(orderId);
        // Ya entregado: el repartidor ya no se mueve -> no persistimos GPS.
        if (isDelivered(delivery)) {
            return mapToDTO(delivery);
        }
        if (request.getLatitude() != null) delivery.setCurrentLatitude(request.getLatitude());
        if (request.getLongitude() != null) delivery.setCurrentLongitude(request.getLongitude());
        if (request.getAccuracy() != null) delivery.setGpsAccuracy(request.getAccuracy());
        if (request.getSpeed() != null) delivery.setGpsSpeed(request.getSpeed());
        if (!"ENTREGADO".equals(delivery.getRouteStatus())) {
            delivery.setRouteStatus("EN_CAMINO");
        }
        return mapToDTO(deliveryRepository.save(delivery));
    }

    @Transactional
    public DeliveryDTO updateDestination(Long orderId, UpdateDestinationRequest request) {
        Delivery delivery = getOrCreateForOrder(orderId);
        // Ya entregado: no se fija/fuerza destino sobre la dirección registrada.
        if (isDelivered(delivery)) {
            return mapToDTO(delivery);
        }
        delivery.setDestinationLatitude(request.getLatitude());
        delivery.setDestinationLongitude(request.getLongitude());
        return mapToDTO(deliveryRepository.save(delivery));
    }

    @Transactional
    public DeliveryDTO updateEta(Long orderId, UpdateEtaRequest request) {
        Delivery delivery = getOrCreateForOrder(orderId);
        // Ya entregado: la ruta está cerrada -> no se actualiza ETA.
        if (isDelivered(delivery)) {
            return mapToDTO(delivery);
        }
        delivery.setEstimatedArrival(request.getEstimatedArrival() != null
                ? request.getEstimatedArrival()
                : LocalDateTime.now().plusMinutes(45));
        return mapToDTO(deliveryRepository.save(delivery));
    }

    private boolean isDelivered(Delivery delivery) {
        if (delivery == null) return false;
        if ("ENTREGADO".equals(delivery.getRouteStatus())) return true;
        Order order = delivery.getOrder();
        return order != null && order.getStatus() == OrderStatus.ENTREGADO;
    }

    private Delivery getOrCreateForOrder(Long orderId) {
        return deliveryRepository.findByOrderId(orderId).orElseGet(() -> {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + orderId));
            // Ya entregado: no crear registro de ruta GPS.
            if (order.getStatus() == OrderStatus.ENTREGADO) {
                throw new ResourceNotFoundException("No existe registro de delivery para el pedido ID: " + orderId + " (pedido ya entregado)");
            }
            return Delivery.builder()
                    .order(order)
                    .driverName(order.getAssignedDriverName() != null ? order.getAssignedDriverName() : "Sin repartidor")
                    .driverPhone(order.getAssignedDriverPhone() != null ? order.getAssignedDriverPhone() : null)
                    .routeStatus("EN_CAMINO")
                    .estimatedArrival(LocalDateTime.now().plusMinutes(45))
                    .build();
        });
    }

    private DeliveryDTO mapToDTO(Delivery d) {
        Order order = d.getOrder();
        return DeliveryDTO.builder()
                .id(d.getId())
                .orderId(order != null ? order.getId() : null)
                .orderNumber(order != null ? order.getOrderNumber() : null)
                .customerName(order != null ? order.getCustomerName() : null)
                .deliveryAddress(order != null ? order.getDeliveryAddress() : null)
                .driverName(d.getDriverName())
                .driverPhone(d.getDriverPhone())
                .routeStatus(d.getRouteStatus())
                .currentLatitude(d.getCurrentLatitude())
                .currentLongitude(d.getCurrentLongitude())
                .destinationLatitude(d.getDestinationLatitude())
                .destinationLongitude(d.getDestinationLongitude())
                .gpsAccuracy(d.getGpsAccuracy())
                .gpsSpeed(d.getGpsSpeed())
                .estimatedArrival(d.getEstimatedArrival())
                .deliveredAt(d.getDeliveredAt())
                .recipientNotes(d.getRecipientNotes())
                .build();
    }
}
