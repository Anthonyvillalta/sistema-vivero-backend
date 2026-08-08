package com.vivero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivero.dto.SupplierDTOs.*;
import com.vivero.entity.*;
import com.vivero.exception.ResourceNotFoundException;
import com.vivero.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final OrderRepository orderRepository;

    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupplierDTO createSupplier(CreateSupplierRequest request, String createdBy) {
        Supplier supplier = Supplier.builder()
                .companyName(request.getCompanyName())
                .contactName(request.getContactName())
                .documentNumber(request.getDocumentNumber())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .active(true)
                .createdBy(createdBy)
                .build();

        supplierRepository.save(supplier);
        return mapToDTO(supplier);
    }

    @Transactional
    public SupplierDTO updateSupplier(Long id, CreateSupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));

        if (request.getCompanyName() != null && !request.getCompanyName().trim().isEmpty()) {
            supplier.setCompanyName(request.getCompanyName().trim());
        }
        if (request.getContactName() != null) supplier.setContactName(request.getContactName().trim());
        if (request.getDocumentNumber() != null) supplier.setDocumentNumber(request.getDocumentNumber().trim());
        if (request.getPhone() != null) supplier.setPhone(request.getPhone().trim());
        if (request.getEmail() != null) supplier.setEmail(request.getEmail().trim());
        if (request.getAddress() != null) supplier.setAddress(request.getAddress().trim());

        supplierRepository.save(supplier);
        return mapToDTO(supplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
        supplier.setActive(false);
        supplierRepository.save(supplier);
    }

    public List<PurchaseDTO> getRecentPurchases() {
        return purchaseRepository.findTop20ByOrderByPurchaseDateDesc().stream()
                .map(this::mapPurchaseToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PurchaseDTO createPurchase(CreatePurchaseRequest request, String createdBy) {
        Supplier supplier = null;
        if (request.getSupplierId() != null) {
            supplier = supplierRepository.findById(request.getSupplierId()).orElse(null);
        }
        if (supplier == null && request.getSupplierName() != null && !request.getSupplierName().trim().isEmpty()) {
            supplier = supplierRepository.findByCompanyNameContainingIgnoreCase(request.getSupplierName().trim())
                    .stream().findFirst().orElse(null);
        }
        if (supplier == null) {
            supplier = supplierRepository.findAll().stream().findFirst().orElse(null);
        }
        if (supplier == null) {
            supplier = supplierRepository.save(Supplier.builder()
                    .companyName(request.getSupplierName() != null && !request.getSupplierName().trim().isEmpty() ? request.getSupplierName().trim() : "Agro Grass del Perú S.A.C.")
                    .contactName("Ing. Roberto Gómez")
                    .phone("+51 955112233")
                    .active(true)
                    .createdBy(createdBy)
                    .build());
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        Purchase purchase = Purchase.builder()
                .purchaseNumber("COM-" + System.currentTimeMillis() % 1000000)
                .supplier(supplier)
                .supplierName(supplier.getCompanyName())
                .status("COMPLETADO")
                .notes(request.getNotes())
                .createdBy(createdBy)
                .items(new ArrayList<>())
                .build();

        for (CreatePurchaseItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + itemReq.getProductId()));

            BigDecimal lineTotal = itemReq.getUnitCost().multiply(itemReq.getQuantity());
            totalAmount = totalAmount.add(lineTotal);

            PurchaseItem item = PurchaseItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .quantity(itemReq.getQuantity())
                    .unitCost(itemReq.getUnitCost())
                    .totalCost(lineTotal)
                    .build();

            purchase.addItem(item);

            // Incrementar stock automáticamente
            BigDecimal currentStock = product.getStock() != null ? product.getStock() : BigDecimal.ZERO;
            BigDecimal newStock = currentStock.add(itemReq.getQuantity());
            product.setStock(newStock);
            product.setCostPrice(itemReq.getUnitCost()); // Actualizar precio costo
            productRepository.save(product);

            // Movimiento de inventario por Entrada de Compra
            InventoryMovement movement = InventoryMovement.builder()
                    .product(product)
                    .movementType(MovementType.ENTRADA)
                    .quantity(itemReq.getQuantity())
                    .previousStock(currentStock)
                    .newStock(newStock)
                    .reason("Compra a proveedor #" + purchase.getPurchaseNumber())
                    .referenceId(purchase.getPurchaseNumber())
                    .createdBy(createdBy)
                    .build();
            inventoryMovementRepository.save(movement);
        }

        purchase.setTotalAmount(totalAmount);
        purchaseRepository.save(purchase);

        // Si la compra requiere delivery / traslado, crear pedido automáticamente en MySQL
        if (Boolean.TRUE.equals(request.getIsDelivery()) || (request.getDeliveryAddress() != null && !request.getDeliveryAddress().trim().isEmpty())) {
            Order order = Order.builder()
                    .orderNumber("#P-" + (10000 + (purchase.getId() % 90000)))
                    .customerName("Proveedor: " + supplier.getCompanyName())
                    .customerPhone(supplier.getPhone() != null ? supplier.getPhone() : "+51 987654321")
                    .deliveryAddress(request.getDeliveryAddress() != null && !request.getDeliveryAddress().trim().isEmpty()
                            ? request.getDeliveryAddress().trim()
                            : (supplier.getAddress() != null ? supplier.getAddress() : "Sede Central Vivero"))
                    .deliveryDate(resolveDeliveryDate(request))
                    .deliveryTimeSlot(request.getDeliveryTimeSlot() != null && !request.getDeliveryTimeSlot().trim().isEmpty()
                            ? request.getDeliveryTimeSlot().trim() : "10:00 AM - 02:00 PM")
                    .status(OrderStatus.PENDIENTE)
                    .deliveryNotes("Compra #" + purchase.getPurchaseNumber() + (request.getDeliveryNotes() != null ? " - " + request.getDeliveryNotes() : ""))
                    .createdBy(createdBy)
                    .build();
            orderRepository.save(order);
        }

        return mapPurchaseToDTO(purchase);
    }

    public SupplierDTO mapToDTO(Supplier supplier) {
        return SupplierDTO.builder()
                .id(supplier.getId())
                .companyName(supplier.getCompanyName())
                .contactName(supplier.getContactName())
                .documentNumber(supplier.getDocumentNumber())
                .phone(supplier.getPhone())
                .email(supplier.getEmail())
                .address(supplier.getAddress())
                .active(supplier.getActive())
                .build();
    }

    public PurchaseDTO mapPurchaseToDTO(Purchase purchase) {
        List<PurchaseItemDTO> itemDTOs = purchase.getItems().stream()
                .map(item -> PurchaseItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitCost(item.getUnitCost())
                        .totalCost(item.getTotalCost())
                        .build())
                .collect(Collectors.toList());

        return PurchaseDTO.builder()
                .id(purchase.getId())
                .purchaseNumber(purchase.getPurchaseNumber())
                .supplierId(purchase.getSupplier().getId())
                .supplierName(purchase.getSupplierName())
                .purchaseDate(purchase.getPurchaseDate())
                .totalAmount(purchase.getTotalAmount())
                .status(purchase.getStatus())
                .notes(purchase.getNotes())
                .items(itemDTOs)
                .build();
    }

    // Explicit deliveryDate field > date embedded in the legacy time slot format > tomorrow.
    private LocalDateTime resolveDeliveryDate(CreatePurchaseRequest request) {
        if (request.getDeliveryDate() != null && !request.getDeliveryDate().trim().isEmpty()) {
            try {
                return LocalDate.parse(request.getDeliveryDate().trim()).atStartOfDay();
            } catch (Exception ignored) {
                // fall through to default
            }
        }
        return LocalDateTime.now().plusDays(1);
    }
}
