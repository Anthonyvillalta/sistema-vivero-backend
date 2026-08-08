package com.vivero.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.dto.WhatsAppDTOs.WhatsAppMessageDTO;
import com.vivero.service.WhatsAppService;

@RestController
@RequestMapping("/whatsapp")
@RequiredArgsConstructor
@Tag(name = "Integración WhatsApp", description = "Generación de Enlaces Directos de WhatsApp Business API")
public class WhatsAppController {

    private final WhatsAppService whatsAppService;

    @GetMapping("/order-confirmation/{orderId}")
    @Operation(summary = "Generar mensaje y enlace de confirmación de pedido para WhatsApp")
    public ResponseEntity<WhatsAppMessageDTO> getOrderConfirmation(@PathVariable Long orderId) {
        return ResponseEntity.ok(whatsAppService.generateOrderConfirmationMessage(orderId));
    }

    @GetMapping("/sale-receipt/{saleId}")
    @Operation(summary = "Generar mensaje y enlace de comprobante de venta para WhatsApp")
    public ResponseEntity<WhatsAppMessageDTO> getSaleReceipt(@PathVariable Long saleId) {
        return ResponseEntity.ok(whatsAppService.generateSaleReceiptMessage(saleId));
    }
}
