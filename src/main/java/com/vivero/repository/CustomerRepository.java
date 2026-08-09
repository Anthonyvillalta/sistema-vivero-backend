package com.vivero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vivero.entity.Customer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByActiveTrue();
    Optional<Customer> findByDocumentNumber(String documentNumber);
    List<Customer> findByFullNameContainingIgnoreCaseOrPhoneContaining(String name, String phone);

    @Query("SELECT c.id as customerId, c.fullName as fullName, c.phone as phone, " +
           "COUNT(s) as purchaseCount, COALESCE(SUM(s.total), 0) as totalPurchases " +
           "FROM Sale s JOIN s.customer c WHERE s.saleDate BETWEEN :startDate AND :endDate " +
           "GROUP BY c.id, c.fullName, c.phone ORDER BY totalPurchases DESC")
    List<Object[]> findTopCustomersBySales(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.id as customerId, c.fullName as fullName, c.phone as phone, " +
           "COUNT(s) as purchaseCount, COALESCE(SUM(s.total), 0) as totalPurchases " +
           "FROM Sale s JOIN s.customer c " +
           "GROUP BY c.id, c.fullName, c.phone ORDER BY totalPurchases DESC")
    List<Object[]> findTopCustomersAllTime();
}
