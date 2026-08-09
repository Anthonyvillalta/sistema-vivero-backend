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

    @Query("SELECT COALESCE(SUM(si.quantity * p.costPrice), 0) " +
           "FROM SaleItem si JOIN si.product p JOIN si.sale s " +
           "WHERE s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal sumCostOfGoodsSoldByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate >= :startDate AND s.saleDate <= :endDate")
    Long countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(AVG(s.total), 0) FROM Sale s WHERE s.saleDate >= :startDate AND s.saleDate <= :endDate")
    BigDecimal avgTotalByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<Sale> findBySaleDateBetweenOrderBySaleDateDesc(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT s.paymentMethod as method, COALESCE(SUM(s.total), 0) as amount, COUNT(s) as count " +
           "FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate GROUP BY s.paymentMethod ORDER BY amount DESC")
    List<Object[]> sumByPaymentMethod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.id as categoryId, c.name as categoryName, SUM(si.totalPrice) as totalAmount, SUM(si.quantity) as totalQuantity " +
           "FROM SaleItem si JOIN si.product p JOIN p.category c JOIN si.sale s " +
           "WHERE s.saleDate BETWEEN :startDate AND :endDate " +
           "GROUP BY c.id, c.name ORDER BY totalAmount DESC")
    List<Object[]> sumSalesByCategory(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(s.saleDate) as saleDate, COALESCE(SUM(s.total), 0) as amount " +
           "FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(s.saleDate) ORDER BY saleDate")
    List<Object[]> dailySalesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate ORDER BY s.saleDate DESC")
    List<Sale> findBySaleDateRangeWithItems(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
