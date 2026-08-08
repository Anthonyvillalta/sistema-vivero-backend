package com.vivero.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivero.dto.DeliveryMethodDTOs.*;
import com.vivero.entity.DeliveryMethod;
import com.vivero.exception.ResourceNotFoundException;
import com.vivero.repository.DeliveryMethodRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryMethodService {

    private final DeliveryMethodRepository deliveryMethodRepository;

    @PostConstruct
    @Transactional
    public void initDefaultDeliveryMethods() {
        if (deliveryMethodRepository.count() == 0) {
            deliveryMethodRepository.save(DeliveryMethod.builder()
                    .name("Delivery a Domicilio Standard")
                    .type("DELIVERY")
                    .price(new BigDecimal("15.00"))
                    .estimatedTime("24 a 48 horas")
                    .description("Envío seguro a domicilio con seguimiento en tiempo real")
                    .active(true)
                    .build());

            deliveryMethodRepository.save(DeliveryMethod.builder()
                    .name("Recojo en Tienda Vivero")
                    .type("STORE")
                    .price(BigDecimal.ZERO)
                    .estimatedTime("Retiro inmediato")
                    .description("Atención y retiro presencial sin recargo en sede principal")
                    .active(true)
                    .build());
        }
    }

    public List<DeliveryMethodDTO> getAllDeliveryMethods() {
        if (deliveryMethodRepository.count() == 0) {
            initDefaultDeliveryMethods();
        }
        return deliveryMethodRepository.findAllByOrderByNameAsc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<DeliveryMethodDTO> getActiveDeliveryMethods() {
        if (deliveryMethodRepository.count() == 0) {
            initDefaultDeliveryMethods();
        }
        return deliveryMethodRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeliveryMethodDTO createDeliveryMethod(CreateDeliveryMethodRequest request) {
        DeliveryMethod method = DeliveryMethod.builder()
                .name(request.getName())
                .type(request.getType() != null ? request.getType() : "DELIVERY")
                .price(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO)
                .estimatedTime(request.getEstimatedTime())
                .description(request.getDescription())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return mapToDTO(deliveryMethodRepository.save(method));
    }

    @Transactional
    public DeliveryMethodDTO updateDeliveryMethod(Long id, CreateDeliveryMethodRequest request) {
        DeliveryMethod method = deliveryMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Método de entrega no encontrado con ID: " + id));

        method.setName(request.getName());
        if (request.getType() != null) method.setType(request.getType());
        if (request.getPrice() != null) method.setPrice(request.getPrice());
        if (request.getEstimatedTime() != null) method.setEstimatedTime(request.getEstimatedTime());
        if (request.getDescription() != null) method.setDescription(request.getDescription());
        if (request.getActive() != null) method.setActive(request.getActive());

        return mapToDTO(deliveryMethodRepository.save(method));
    }

    @Transactional
    public DeliveryMethodDTO toggleDeliveryMethodStatus(Long id) {
        DeliveryMethod method = deliveryMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Método de entrega no encontrado con ID: " + id));

        method.setActive(!Boolean.TRUE.equals(method.getActive()));
        return mapToDTO(deliveryMethodRepository.save(method));
    }

    private DeliveryMethodDTO mapToDTO(DeliveryMethod method) {
        return DeliveryMethodDTO.builder()
                .id(method.getId())
                .name(method.getName())
                .type(method.getType())
                .price(method.getPrice())
                .estimatedTime(method.getEstimatedTime())
                .description(method.getDescription())
                .active(method.getActive())
                .build();
    }
}
