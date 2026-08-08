package com.vivero.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivero.dto.AIDTOs.ProductAnalysisDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private static final String GEMINI_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent";

    private static final List<String> VALID_CATEGORIES = List.of(
            "Grass Natural", "Plantas Ornamentales", "Árboles y Palmeras", "Accesorios e Insumos"
    );

    private static final String PROMPT = """
            Eres un botánico experto y asesor comercial de un vivero peruano.
            Analiza la imagen del producto capturada con la cámara y responde SOLO con un JSON válido,
            sin markdown ni texto adicional, con exactamente estos campos:
            - "name": nombre comercial corto del producto (máximo 60 caracteres, en español).
            - "categoryName": una y solo una de estas 4 categorías exactas: 'Grass Natural',
              'Plantas Ornamentales', 'Árboles y Palmeras', 'Accesorios e Insumos'.
              Si es una manta de césped, grass en rollo o pasto por m2 -> 'Grass Natural'.
              Si es una planta de interior/exterior, flor o arbusto ornamental -> 'Plantas Ornamentales'.
              Si es un árbol, palmera o planta de gran porte -> 'Árboles y Palmeras'.
              Si es maceta, tierra, abono, herramienta, insumo o accesorio de jardinería -> 'Accesorios e Insumos'.
            - "description": descripción breve y comercial de 1 a 2 frases (máximo 150 caracteres).
            - "imageUrl": una URL pública directa (jpg o png) de una foto representativa del producto,
              preferiblemente de Wikimedia Commons (https://upload.wikimedia.org/wikipedia/commons/...).
              Si no puedes garantizar una URL pública válida, devuelve una cadena vacía "".
            """;

    private final CompanySettingsService companySettingsService;
    private final ObjectMapper objectMapper;

    public ProductAnalysisDTO analyzeProductImage(String base64Image, String mimeType) {
        String apiKey = companySettingsService.getGeminiApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Aún no has configurado la clave API de Google Gemini. Ve a Configuración → Datos de la Empresa → IA & Escáner.");
        }

        Map<String, Object> inlineData = new LinkedHashMap<>();
        inlineData.put("mimeType", mimeType != null && !mimeType.isBlank() ? mimeType : "image/jpeg");
        inlineData.put("data", base64Image);

        Map<String, Object> part = new LinkedHashMap<>();
        part.put("text", PROMPT);
        Map<String, Object> imagePart = new LinkedHashMap<>();
        imagePart.put("inlineData", inlineData);

        Map<String, Object> generationConfig = new LinkedHashMap<>();
        generationConfig.put("temperature", 0.2);
        generationConfig.put("maxOutputTokens", 1024);
        generationConfig.put("responseMimeType", "application/json");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("contents", List.of(Map.of("parts", List.of(part, imagePart))));
        body.put("generationConfig", generationConfig);

        String responseJson = buildRestClient().post()
                .uri(GEMINI_ENDPOINT + "?key=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        return parseResponse(responseJson);
    }

    private RestClient buildRestClient() {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(90));
        return RestClient.builder().requestFactory(factory).build();
    }

    private ProductAnalysisDTO parseResponse(String responseJson) {
        try {
            JsonNode root = objectMapper.readTree(responseJson);
            String text = root.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText("");

            if (text.isBlank()) {
                JsonNode message = root.path("promptFeedback").path("blockReason");
                String reason = message.isMissingNode() ? "" : message.asText();
                return ProductAnalysisDTO.builder()
                        .message("La IA no pudo analizar la imagen" + (reason.isBlank() ? "." : " (motivo: " + reason + ")."))
                        .build();
            }

            String clean = text.trim();
            if (clean.startsWith("```")) {
                clean = clean.replaceFirst("^```(json)?", "").replaceFirst("```$", "").trim();
            }

            JsonNode json = objectMapper.readTree(clean);
            String category = json.path("categoryName").asText("").trim();
            if (!VALID_CATEGORIES.contains(category)) {
                category = "Plantas Ornamentales";
            }

            return ProductAnalysisDTO.builder()
                    .name(truncate(json.path("name").asText("").trim(), 60))
                    .categoryName(category)
                    .description(truncate(json.path("description").asText("").trim(), 150))
                    .imageUrl(json.path("imageUrl").asText("").trim())
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo interpretar la respuesta de la IA. Inténtalo de nuevo.");
        }
    }

    private String truncate(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max).trim();
    }
}
