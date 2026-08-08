package com.vivero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.vivero.dto.WhatsAppDTOs.WhatsAppMessageDTO;
import com.vivero.entity.Order;
import com.vivero.entity.Sale;
import com.vivero.exception.ResourceNotFoundException;
import com.vivero.repository.OrderRepository;
import com.vivero.repository.SaleRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class WhatsAppService {

    private final SaleRepository saleRepository;
    private final OrderRepository orderRepository;

    public WhatsAppMessageDTO generateOrderConfirmationMessage(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + orderId));

        String phone = formatPhone(order.getCustomerPhone());
        String name = order.getCustomerName();

        StringBuilder sb = new StringBuilder();
        sb.append("Hola ").append(name).append(" 👋\n");
        sb.append("Gracias por comprar en VIVERO 🌱\n\n");
        sb.append("Tu pedido: ").append(order.getOrderNumber()).append("\n");
        if (order.getSale() != null && !order.getSale().getItems().isEmpty()) {
            order.getSale().getItems().forEach(item ->
                    sb.append("• ").append(item.getProductName()).append(" ").append(item.getQuantity())
                            .append(" ").append(item.getUnitType() == com.vivero.entity.UnitType.M2 ? "m²" : "und").append("\n"));
        }
        sb.append("\nDirección de entrega: ").append(order.getDeliveryAddress()).append("\n");
        sb.append("Horario estimado: ").append(order.getDeliveryTimeSlot() != null ? order.getDeliveryTimeSlot() : "Hoy").append("\n\n");
        sb.append("Gracias por confiar en nosotros.");

        String message = sb.toString();
        String waLink = "https://wa.me/" + phone + "?text=" + URLEncoder.encode(message, StandardCharsets.UTF_8);

        return WhatsAppMessageDTO.builder()
                .phone(phone)
                .customerName(name)
                .message(message)
                .waLink(waLink)
                .build();
    }

    public WhatsAppMessageDTO generateSaleReceiptMessage(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + saleId));

        String phone = formatPhone(sale.getCustomerPhone());
        String name = sale.getCustomerName();

        StringBuilder sb = new StringBuilder();
        sb.append("Hola ").append(name).append(" 👋\n");
        sb.append("Adjuntamos tu comprobante digital de VIVERO 🌱\n\n");
        sb.append("N° Comprobante: ").append(sale.getReceiptNumber()).append("\n");
        sb.append("Total: S/ ").append(sale.getTotal()).append("\n");
        sb.append("Método de pago: ").append(sale.getPaymentMethod()).append("\n\n");
        sb.append("¡Agradecemos tu preferencia!");

        String message = sb.toString();
        String waLink = "https://wa.me/" + phone + "?text=" + URLEncoder.encode(message, StandardCharsets.UTF_8);

        return WhatsAppMessageDTO.builder()
                .phone(phone)
                .customerName(name)
                .message(message)
                .waLink(waLink)
                .build();
    }

    private String formatPhone(String phone) {
        if (phone == null) return "51987654321";
        String clean = phone.replaceAll("[^0-9]", "");
        if (!clean.startsWith("51") && clean.length() == 9) {
            clean = "51" + clean;
        }
        return clean;
    }
}
