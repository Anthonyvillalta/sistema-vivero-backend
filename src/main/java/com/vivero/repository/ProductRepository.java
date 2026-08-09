package com.vivero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vivero.entity.Product;

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

    @Query("SELECT p.id, p.name, p.variety, c.name, SUM(si.quantity) as qty, SUM(si.totalPrice) as revenue " +
           "FROM SaleItem si JOIN si.product p JOIN p.category c JOIN si.sale s " +
           "WHERE s.saleDate BETWEEN :startDate AND :endDate " +
           "GROUP BY p.id, p.name, p.variety, c.name ORDER BY revenue DESC")
    List<Object[]> findTopProductsBySales(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.id as categoryId, c.name as categoryName, SUM(p.stock * p.costPrice) as totalValue, SUM(p.stock) as totalQty " +
           "FROM Product p JOIN p.category c WHERE p.active = true AND p.stock > 0 " +
           "GROUP BY c.id, c.name ORDER BY totalValue DESC")
    List<Object[]> inventoryValuationByCategory();

    @Query("SELECT p FROM Product p WHERE p.active = true " +
           "ORDER BY (p.stock * p.costPrice) DESC")
    List<Product> findAllWithStockValue();

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.active = true")
    List<Product> findAllWithCategory();
}
