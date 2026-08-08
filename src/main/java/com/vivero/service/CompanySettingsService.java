package com.vivero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivero.dto.SettingsDTOs.CompanySettingsDTO;
import com.vivero.entity.CompanySettings;
import com.vivero.repository.CompanySettingsRepository;

@Service
@RequiredArgsConstructor
public class CompanySettingsService {

    private final CompanySettingsRepository companySettingsRepository;

    @Transactional(readOnly = true)
    public CompanySettingsDTO getSettings() {
        CompanySettings settings = getOrCreateDefault();
        return mapToDTO(settings);
    }

    @Transactional
    public CompanySettingsDTO updateSettings(CompanySettingsDTO request) {
        CompanySettings settings = getOrCreateDefault();
        settings.setCompanyName(request.getCompanyName());
        settings.setCompanyRuc(request.getCompanyRuc());
        settings.setCompanyPhone(request.getCompanyPhone());
        settings.setCompanyAddress(request.getCompanyAddress());
        settings.setGeminiApiKey(request.getGeminiApiKey());
        if (request.getWarehouseLatitude() != null) {
            settings.setWarehouseLatitude(request.getWarehouseLatitude());
        }
        if (request.getWarehouseLongitude() != null) {
            settings.setWarehouseLongitude(request.getWarehouseLongitude());
        }
        CompanySettings saved = companySettingsRepository.save(settings);
        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public String getGeminiApiKey() {
        return getOrCreateDefault().getGeminiApiKey();
    }

    private CompanySettingsDTO mapToDTO(CompanySettings s) {
        return CompanySettingsDTO.builder()
                .companyName(s.getCompanyName())
                .companyRuc(s.getCompanyRuc())
                .companyPhone(s.getCompanyPhone())
                .companyAddress(s.getCompanyAddress())
                .warehouseLatitude(s.getWarehouseLatitude())
                .warehouseLongitude(s.getWarehouseLongitude())
                .geminiApiKey(s.getGeminiApiKey())
                .build();
    }

    private CompanySettings getOrCreateDefault() {
        return companySettingsRepository.findTopByOrderByIdAsc()
                .orElseGet(() -> companySettingsRepository.save(CompanySettings.builder().build()));
    }
}
