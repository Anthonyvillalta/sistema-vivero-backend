package com.vivero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vivero.entity.Sale;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findTop20ByOrderBySaleDateDesc();

    @Query("SELECT MAX(s.id) FROM Sale s")
    Long findMaxId();

    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s WHERE s.saleDate >= :startDate AND s.saleDate <= :endDate")
    BigDecimal sumTotalByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<Sale> findBySaleDateBetweenOrderBySaleDateDesc(LocalDateTime startDate, LocalDateTime endDate);
}
