package com.vivero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vivero.entity.District;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    List<District> findByProvinceIdAndActiveTrueOrderByNameAsc(Long provinceId);
    List<District> findByProvinceIdOrderByNameAsc(Long provinceId);
    List<District> findByActiveTrueOrderByNameAsc();
    List<District> findAllByOrderByNameAsc();
    java.util.Optional<District> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
}
