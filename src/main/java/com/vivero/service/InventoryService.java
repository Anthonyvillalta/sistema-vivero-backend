package com.vivero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivero.dto.InventoryDTOs.*;
import com.vivero.entity.InventoryMovement;
import com.vivero.entity.MovementType;
import com.vivero.entity.Product;
import com.vivero.exception.BadRequestException;
import com.vivero.exception.ResourceNotFoundException;
import com.vivero.repository.InventoryMovementRepository;
import com.vivero.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final InventoryMovementRepository inventoryMovementRepository;

    public List<InventoryMovementDTO> getRecentMovements() {
        return inventoryMovementRepository.findTop20ByOrderByCreatedAtDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryMovementDTO> getMovementsByProduct(Long productId) {
        return inventoryMovementRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryMovementDTO adjustStock(StockAdjustmentRequest request, String createdBy) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getProductId()));

        BigDecimal currentStock = product.getStock() != null ? product.getStock() : BigDecimal.ZERO;
        BigDecimal currentReserved = product.getReservedStock() != null ? product.getReservedStock() : BigDecimal.ZERO;
        BigDecimal newStock = currentStock;

        if (request.getMovementType() == MovementType.ENTRADA) {
            newStock = currentStock.add(request.getQuantity());
            product.setStock(newStock);
        } else if (request.getMovementType() == MovementType.SALIDA) {
            if (currentStock.compareTo(request.getQuantity()) < 0) {
                throw new BadRequestException("Stock insuficiente para realizar la salida. Stock actual: " + currentStock);
            }
            newStock = currentStock.subtract(request.getQuantity());
            product.setStock(newStock);
        } else if (request.getMovementType() == MovementType.MERMA) {
            if (currentStock.compareTo(request.getQuantity()) < 0) {
                throw new BadRequestException("Stock insuficiente para registrar la merma. Stock actual: " + currentStock);
            }
            newStock = currentStock.subtract(request.getQuantity());
            product.setStock(newStock);
        } else if (request.getMovementType() == MovementType.RESERVA) {
            BigDecimal available = product.getAvailableStock();
            if (available.compareTo(request.getQuantity()) < 0) {
                throw new BadRequestException("Stock disponible insuficiente para realizar la reserva. Disponible: " + available);
            }
            BigDecimal updatedReserved = currentReserved.add(request.getQuantity());
            product.setReservedStock(updatedReserved);
            newStock = currentStock; // Physical stock remains unchanged
        } else if (request.getMovementType() == MovementType.LIBERAR_RESERVA) {
            if (currentReserved.compareTo(request.getQuantity()) < 0) {
                throw new BadRequestException("No es posible liberar más stock del que se encuentra actualmente reservado (Reservado: " + currentReserved + ").");
            }
            BigDecimal updatedReserved = currentReserved.subtract(request.getQuantity());
            product.setReservedStock(updatedReserved);
            newStock = currentStock; // Physical stock remains unchanged, availableStock increases
        } else if (request.getMovementType() == MovementType.AJUSTE) {
            newStock = request.getQuantity();
            product.setStock(newStock);
        }

        productRepository.save(product);

        BigDecimal prevMovementStock = currentStock;
        BigDecimal newMovementStock = newStock;

        if (request.getMovementType() == MovementType.RESERVA || request.getMovementType() == MovementType.LIBERAR_RESERVA) {
            prevMovementStock = currentStock.subtract(currentReserved);
            newMovementStock = product.getAvailableStock();
        }

        InventoryMovement movement = InventoryMovement.builder()
                .product(product)
                .movementType(request.getMovementType())
                .quantity(request.getQuantity())
                .previousStock(prevMovementStock)
                .newStock(newMovementStock)
                .reason(request.getReason() != null ? request.getReason() : "Ajuste manual de inventario")
                .createdBy(createdBy)
                .build();

        inventoryMovementRepository.save(movement);
        return mapToDTO(movement);
    }

    private InventoryMovementDTO mapToDTO(InventoryMovement movement) {
        return InventoryMovementDTO.builder()
                .id(movement.getId())
                .productId(movement.getProduct().getId())
                .productName(movement.getProduct().getName())
                .unitType(movement.getProduct().getUnitType().name())
                .movementType(movement.getMovementType())
                .quantity(movement.getQuantity())
                .previousStock(movement.getPreviousStock())
                .newStock(movement.getNewStock())
                .reason(movement.getReason())
                .referenceId(movement.getReferenceId())
                .createdAt(movement.getCreatedAt())
                .createdBy(movement.getCreatedBy())
                .build();
    }
}
