package com.vivero.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.dto.AIDTOs.AnalyzeProductRequest;
import com.vivero.dto.AIDTOs.ProductAnalysisDTO;
import com.vivero.service.GeminiService;

import java.util.Base64;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Tag(name = "Inteligencia Artificial", description = "Análisis de imágenes de productos con Google Gemini")
public class AIController {

    private static final long MAX_IMAGE_BYTES = 6L * 1024 * 1024;

    private final GeminiService geminiService;

    @PostMapping("/analyze-product")
    @Operation(summary = "Analizar foto de producto con IA y devolver datos sugeridos para el formulario")
    public ResponseEntity<ProductAnalysisDTO> analyzeProduct(@RequestBody AnalyzeProductRequest request) {
        if (request.getImage() == null || request.getImage().isBlank()) {
            throw new IllegalArgumentException("La imagen capturada está vacía.");
        }

        String base64 = request.getImage();
        String prefix = "base64,";
        int commaIndex = base64.indexOf(prefix);
        if (commaIndex >= 0) {
            base64 = base64.substring(commaIndex + prefix.length());
        }

        byte[] decoded = Base64.getDecoder().decode(base64);
        if (decoded.length > MAX_IMAGE_BYTES) {
            throw new IllegalArgumentException("La imagen es demasiado grande. Toma una foto más cercana o comprime la imagen.");
        }

        return ResponseEntity.ok(geminiService.analyzeProductImage(base64, request.getMimeType()));
    }
}
