package com.vivero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vivero.entity.Expense;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findTop20ByOrderByExpenseDateDesc();

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.expenseDate >= :startDate AND e.expenseDate <= :endDate")
    BigDecimal sumTotalByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(e) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    Long countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e.category as category, COALESCE(SUM(e.amount), 0) as amount, COUNT(e) as count " +
           "FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY e.category ORDER BY amount DESC")
    List<Object[]> sumByCategory(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT CAST(e.expenseDate AS date) as expenseDate, COALESCE(SUM(e.amount), 0) as amount " +
           "FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(e.expenseDate AS date) ORDER BY expenseDate")
    List<Object[]> dailyExpensesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<Expense> findByExpenseDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
