package com.vivero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vivero.entity.DeliveryMethod;

import java.util.List;

@Repository
public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Long> {
    List<DeliveryMethod> findByActiveTrueOrderByNameAsc();
    List<DeliveryMethod> findAllByOrderByNameAsc();
}
