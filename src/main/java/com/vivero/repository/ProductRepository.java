package com.vivero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vivero.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();
    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);
    Optional<Product> findByCode(String code);

    @Query("SELECT p FROM Product p WHERE p.active = true AND (p.stock - p.reservedStock) <= p.minStock")
    List<Product> findCriticalStockProducts();

    @Query("SELECT p.id, p.name, p.variety, c.name, SUM(si.quantity) as qty, SUM(si.totalPrice) as revenue, COALESCE(SUM(si.quantity * p.costPrice), 0) as totalCost " +
           "FROM SaleItem si JOIN si.product p JOIN p.category c JOIN si.sale s " +
           "WHERE s.saleDate BETWEEN :startDate AND :endDate " +
           "GROUP BY p.id, p.name, p.variety, c.name ORDER BY revenue DESC")
    List<Object[]> findTopProductsBySales(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.id as categoryId, c.name as categoryName, SUM(p.stock * p.costPrice) as totalValue, SUM(p.stock) as totalQty " +
           "FROM Product p JOIN p.category c WHERE p.active = true AND p.stock > 0 " +
           "GROUP BY c.id, c.name ORDER BY totalValue DESC")
    List<Object[]> inventoryValuationByCategory();

    @Query("SELECT c.id, c.name, SUM(p.stock * p.costPrice) as totalCost, SUM(p.stock * p.price) as totalRevenue, SUM((p.price - p.costPrice) * p.stock) as totalProfit, COUNT(p) as totalProducts, SUM(p.stock) as totalQty " +
           "FROM Product p JOIN p.category c WHERE p.active = true AND p.stock > 0 " +
           "GROUP BY c.id, c.name ORDER BY totalCost DESC")
    List<Object[]> inventoryValuationDetailByCategory();

    @Query("SELECT COALESCE(SUM(p.stock), 0) FROM Product p WHERE p.active = true")
    BigDecimal sumTotalStock();

    @Query("SELECT COALESCE(SUM(p.stock - p.reservedStock), 0) FROM Product p WHERE p.active = true")
    BigDecimal sumAvailableStock();

    @Query("SELECT COUNT(DISTINCT p.name) FROM Product p WHERE p.active = true")
    Long countDistinctProductNames();

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stock > 0 ORDER BY (p.stock * p.costPrice) DESC")
    List<Product> findAllWithStockValue();

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.active = true")
    List<Product> findAllWithCategory();
}
