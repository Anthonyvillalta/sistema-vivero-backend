package com.vivero.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.dto.SettingsDTOs.CompanySettingsDTO;
import com.vivero.service.CompanySettingsService;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
@Tag(name = "Configuración del Sistema", description = "Configuración general y claves de servicios externos (IA)")
public class CompanySettingsController {

    private final CompanySettingsService companySettingsService;

    @GetMapping("/company")
    @Operation(summary = "Obtener configuración general del sistema")
    public ResponseEntity<CompanySettingsDTO> getSettings() {
        return ResponseEntity.ok(companySettingsService.getSettings());
    }

    @PutMapping("/company")
    @Operation(summary = "Actualizar configuración general del sistema")
    public ResponseEntity<CompanySettingsDTO> updateSettings(@RequestBody CompanySettingsDTO request) {
        return ResponseEntity.ok(companySettingsService.updateSettings(request));
    }
}
