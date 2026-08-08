package com.vivero.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vivero.entity.CompanySettings;

import java.util.Optional;

public interface CompanySettingsRepository extends JpaRepository<CompanySettings, Long> {
    Optional<CompanySettings> findTopByOrderByIdAsc();
}
