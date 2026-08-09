package com.vivero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vivero.entity.Purchase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findTop20ByOrderByPurchaseDateDesc();

    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Purchase p WHERE p.purchaseDate BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(p) FROM Purchase p WHERE p.purchaseDate BETWEEN :startDate AND :endDate")
    Long countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s.id as supplierId, s.companyName as supplierName, COALESCE(SUM(p.totalAmount), 0) as amount, COUNT(p) as purchaseCount " +
           "FROM Purchase p JOIN p.supplier s WHERE p.purchaseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY s.id, s.companyName ORDER BY amount DESC")
    List<Object[]> sumBySupplier(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT CAST(p.purchaseDate AS date) as purchaseDate, COALESCE(SUM(p.totalAmount), 0) as amount " +
           "FROM Purchase p WHERE p.purchaseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(p.purchaseDate AS date) ORDER BY purchaseDate")
    List<Object[]> dailyPurchasesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
