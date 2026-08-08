package com.vivero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivero.dto.SaleDTOs.*;
import com.vivero.entity.*;
import com.vivero.exception.BadRequestException;
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
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final InventoryMovementRepository inventoryMovementRepository;

    public List<SaleDTO> getRecentSales() {
        return saleRepository.findTop20ByOrderBySaleDateDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public SaleDTO getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + id));
        return mapToDTO(sale);
    }

    @Transactional
    public SaleDTO createSale(CreateSaleRequest request, String sellerUsername) {
        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId()).orElse(null);
        }

        String customerName = request.getCustomerName();
        if (customerName == null && customer != null) {
            customerName = customer.getFullName();
        }
        if (customerName == null) customerName = "Cliente General";

        String customerPhone = request.getCustomerPhone();
        if (customerPhone == null && customer != null) {
            customerPhone = customer.getPhone();
        }
        if (customerPhone == null) customerPhone = "N/A";

        BigDecimal subtotal = BigDecimal.ZERO;

        Long maxId = saleRepository.findMaxId();
        long nextSequence = (maxId != null ? maxId : 0L) + 1;
        String receiptNumber = String.format("VNT-%d-%04d", LocalDate.now().getYear(), nextSequence % 10000);

        Sale sale = Sale.builder()
                .receiptNumber(receiptNumber)
                .customer(customer)
                .customerName(customerName)
                .customerPhone(customerPhone)
                .deliveryFee(request.getDeliveryFee() != null ? request.getDeliveryFee() : BigDecimal.ZERO)
                .discount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus("PAGADO")
                .sellerUsername(sellerUsername)
                .createdBy(sellerUsername)
                .items(new ArrayList<>())
                .build();

        for (CreateSaleItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + itemReq.getProductId()));

            // Descontar stock
            BigDecimal currentStock = product.getStock() != null ? product.getStock() : BigDecimal.ZERO;
            if (currentStock.compareTo(itemReq.getQuantity()) < 0) {
                throw new BadRequestException("Stock insuficiente para: " + product.getName() + ". Disponible: " + currentStock);
            }

            BigDecimal itemTotal = itemReq.getUnitPrice().multiply(itemReq.getQuantity());
            subtotal = subtotal.add(itemTotal);

            SaleItem item = SaleItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .unitType(product.getUnitType())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .totalPrice(itemTotal)
                    .build();

            sale.addItem(item);

            // Actualizar stock del producto
            BigDecimal newStock = currentStock.subtract(itemReq.getQuantity());
            product.setStock(newStock);
            productRepository.save(product);

            // Registrar movimiento de inventario
            InventoryMovement movement = InventoryMovement.builder()
                    .product(product)
                    .movementType(MovementType.SALIDA)
                    .quantity(itemReq.getQuantity())
                    .previousStock(currentStock)
                    .newStock(newStock)
                    .reason("Venta #" + sale.getReceiptNumber())
                    .referenceId(sale.getReceiptNumber())
                    .createdBy(sellerUsername)
                    .build();
            inventoryMovementRepository.save(movement);
        }

        sale.setSubtotal(subtotal);
        BigDecimal total = subtotal.add(sale.getDeliveryFee()).subtract(sale.getDiscount());
        sale.setTotal(total);

        saleRepository.save(sale);

        // Actualizar datos de compras del cliente
        if (customer != null) {
            BigDecimal prevPurchases = customer.getTotalPurchases() != null ? customer.getTotalPurchases() : BigDecimal.ZERO;
            customer.setTotalPurchases(prevPurchases.add(total));
            customer.setLastPurchaseDate(LocalDateTime.now());
            if (customer.getTotalPurchases().compareTo(new BigDecimal("1000")) >= 0) {
                customer.setIsFrequent(true);
            }
            customerRepository.save(customer);
        }

        // Si requiere delivery, crear pedido automáticamente en MySQL
        boolean isDeliveryOrder = Boolean.TRUE.equals(request.getCreateOrderForDelivery())
                || (request.getDeliveryAddress() != null && !request.getDeliveryAddress().trim().isEmpty() && !request.getDeliveryAddress().toLowerCase().contains("recojo en tienda"))
                || (sale.getDeliveryFee() != null && sale.getDeliveryFee().compareTo(BigDecimal.ZERO) > 0);

        if (isDeliveryOrder) {
            Order order = Order.builder()
                    .orderNumber("#P-" + (10000 + (sale.getId() % 90000)))
                    .sale(sale)
                    .customer(customer)
                    .customerName(customerName)
                    .customerPhone(customerPhone)
                    .deliveryAddress(request.getDeliveryAddress() != null && !request.getDeliveryAddress().trim().isEmpty()
                            ? request.getDeliveryAddress().trim()
                            : (customer != null && customer.getAddress() != null ? customer.getAddress() : "Dirección de entrega"))
                    .deliveryDate(resolveDeliveryDate(request))
                    .deliveryTimeSlot(request.getDeliveryTimeSlot() != null && !request.getDeliveryTimeSlot().trim().isEmpty()
                            ? cleanTimeSlot(request.getDeliveryTimeSlot()) : "10:00 AM - 02:00 PM")
                    .status(OrderStatus.PENDIENTE)
                    .createdBy(sellerUsername)
                    .build();
            orderRepository.save(order);
        }

        return mapToDTO(sale);
    }

    public SaleDTO mapToDTO(Sale sale) {
        List<SaleItemDTO> itemDTOs = sale.getItems().stream()
                .map(item -> SaleItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProductName())
                        .unitType(item.getUnitType())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return SaleDTO.builder()
                .id(sale.getId())
                .receiptNumber(sale.getReceiptNumber())
                .customerId(sale.getCustomer() != null ? sale.getCustomer().getId() : null)
                .customerName(sale.getCustomerName())
                .customerPhone(sale.getCustomerPhone())
                .saleDate(sale.getSaleDate())
                .subtotal(sale.getSubtotal())
                .deliveryFee(sale.getDeliveryFee())
                .discount(sale.getDiscount())
                .total(sale.getTotal())
                .paymentMethod(sale.getPaymentMethod())
                .paymentStatus(sale.getPaymentStatus())
                .sellerUsername(sale.getSellerUsername())
                .items(itemDTOs)
                .build();
    }

    // Resolves the real delivery date: explicit field > date embedded in the old
    // "yyyy-MM-dd (turno)" time slot format > fallback tomorrow.
    private LocalDateTime resolveDeliveryDate(CreateSaleRequest request) {
        if (request.getDeliveryDate() != null && !request.getDeliveryDate().trim().isEmpty()) {
            try {
                return LocalDate.parse(request.getDeliveryDate().trim()).atStartOfDay();
            } catch (Exception ignored) {
                // fall through to next strategy
            }
        }
        if (request.getDeliveryTimeSlot() != null) {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d{4}-\\d{2}-\\d{2})").matcher(request.getDeliveryTimeSlot());
            if (m.find()) {
                try {
                    return LocalDate.parse(m.group(1)).atStartOfDay();
                } catch (Exception ignored) {
                    // fall through to default
                }
            }
        }
        return LocalDateTime.now().plusDays(1);
    }

    // Removes the legacy "yyyy-MM-dd (" date prefix from the stored time slot
    // so the turn is clean (e.g. "10:00 AM - 02:00 PM (Tarde) - Repartidor: X").
    private String cleanTimeSlot(String raw) {
        String cleaned = raw.trim();
        cleaned = cleaned.replaceFirst("^\\d{4}-\\d{2}-\\d{2}\\s*\\(", "");
        cleaned = cleaned.replaceFirst("\\)\\s*$", "");
        return cleaned.trim();
    }
}
