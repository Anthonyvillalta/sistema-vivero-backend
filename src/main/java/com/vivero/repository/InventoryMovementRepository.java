package com.vivero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vivero.entity.InventoryMovement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    List<InventoryMovement> findByProductIdOrderByCreatedAtDesc(Long productId);
    List<InventoryMovement> findTop20ByOrderByCreatedAtDesc();

    @Query("SELECT m FROM InventoryMovement m WHERE m.createdAt BETWEEN :startDate AND :endDate ORDER BY m.createdAt DESC")
    List<InventoryMovement> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT m.movementType as type, COUNT(m) as count, COALESCE(SUM(m.quantity), 0) as totalQuantity " +
           "FROM InventoryMovement m WHERE m.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY m.movementType ORDER BY totalQuantity DESC")
    List<Object[]> sumByType(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT m FROM InventoryMovement m JOIN FETCH m.product WHERE m.createdAt BETWEEN :startDate AND :endDate ORDER BY m.createdAt DESC")
    List<InventoryMovement> findByDateRangeWithProduct(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
