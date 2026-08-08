package com.vivero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivero.dto.OrderDTOs.*;
import com.vivero.entity.*;
import com.vivero.exception.ResourceNotFoundException;
import com.vivero.repository.DeliveryRepository;
import com.vivero.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findTop20ByOrderByCreatedAtDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        return mapToDTO(order);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));

        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
        }
        if (request.getDriverName() != null) {
            order.setAssignedDriverName(request.getDriverName());
        }
        if (request.getDriverPhone() != null) {
            order.setAssignedDriverPhone(request.getDriverPhone());
        }

        orderRepository.save(order);

        // Si cambia a EN_DELIVERY, crear/actualizar registro de Delivery
        if (order.getStatus() == OrderStatus.EN_DELIVERY) {
            Delivery delivery = deliveryRepository.findByOrderId(order.getId()).orElse(
                    Delivery.builder()
                            .order(order)
                            .driverName(order.getAssignedDriverName() != null ? order.getAssignedDriverName() : "Carlos Delivery")
                            .driverPhone(order.getAssignedDriverPhone() != null ? order.getAssignedDriverPhone() : "+51 987654323")
                            .routeStatus("EN_CAMINO")
                            .estimatedArrival(LocalDateTime.now().plusMinutes(45))
                            .build()
            );
            delivery.setRouteStatus("EN_CAMINO");
            deliveryRepository.save(delivery);
        } else if (order.getStatus() == OrderStatus.ENTREGADO) {
            deliveryRepository.findByOrderId(order.getId()).ifPresent(del -> {
                del.setRouteStatus("ENTREGADO");
                del.setDeliveredAt(LocalDateTime.now());
                if (request.getNotes() != null) del.setRecipientNotes(request.getNotes());
                deliveryRepository.save(del);
            });
        }

        return mapToDTO(order);
    }

    public OrderDTO mapToDTO(Order order) {
        DeliveryDTO deliveryDTO = deliveryRepository.findByOrderId(order.getId())
                .map(d -> DeliveryDTO.builder()
                        .id(d.getId())
                        .orderId(d.getOrder().getId())
                        .orderNumber(order.getOrderNumber())
                        .customerName(order.getCustomerName())
                        .deliveryAddress(order.getDeliveryAddress())
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
                        .build())
                .orElse(null);

        String productsSummary = "";
        if (order.getSale() != null && order.getSale().getItems() != null && !order.getSale().getItems().isEmpty()) {
            productsSummary = order.getSale().getItems().stream()
                    .map(i -> i.getProductName() + " (" + i.getQuantity() + " " + ("M2".equals(i.getUnitType() != null ? i.getUnitType().name() : "") ? "m²" : "und") + ")")
                    .collect(Collectors.joining(", "));
        } else if (order.getDeliveryNotes() != null && !order.getDeliveryNotes().trim().isEmpty()) {
            productsSummary = order.getDeliveryNotes();
        } else {
            productsSummary = "Plantas / Productos de Vivero";
        }

        return OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .saleId(order.getSale() != null ? order.getSale().getId() : null)
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryDate(order.getDeliveryDate())
                .deliveryTimeSlot(order.getDeliveryTimeSlot())
                .status(order.getStatus())
                .assignedDriverName(order.getAssignedDriverName())
                .assignedDriverPhone(order.getAssignedDriverPhone())
                .deliveryNotes(order.getDeliveryNotes())
                .productsSummary(productsSummary)
                .createdAt(order.getCreatedAt())
                .delivery(deliveryDTO)
                .build();
    }
}
