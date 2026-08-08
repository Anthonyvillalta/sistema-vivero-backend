package com.vivero.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", length = 150)
    private String companyName;

    @Column(name = "company_ruc", length = 20)
    private String companyRuc;

    @Column(name = "company_phone", length = 30)
    private String companyPhone;

    @Column(name = "company_address", columnDefinition = "TEXT")
    private String companyAddress;

    @Column(name = "warehouse_latitude", precision = 10, scale = 8)
    private BigDecimal warehouseLatitude;

    @Column(name = "warehouse_longitude", precision = 11, scale = 8)
    private BigDecimal warehouseLongitude;

    @Column(name = "gemini_api_key", length = 255)
    private String geminiApiKey;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
