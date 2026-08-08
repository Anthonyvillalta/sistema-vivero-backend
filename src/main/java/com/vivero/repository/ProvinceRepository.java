package com.vivero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vivero.entity.Province;

import java.util.List;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {
    List<Province> findByDepartmentIdAndActiveTrueOrderByNameAsc(Long departmentId);
    List<Province> findByDepartmentIdOrderByNameAsc(Long departmentId);
    List<Province> findByActiveTrueOrderByNameAsc();
    List<Province> findAllByOrderByNameAsc();
    java.util.Optional<Province> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
}
