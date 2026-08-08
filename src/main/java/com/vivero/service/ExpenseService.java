package com.vivero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivero.dto.ExpenseDTOs.*;
import com.vivero.entity.Expense;
import com.vivero.repository.ExpenseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public List<ExpenseDTO> getRecentExpenses() {
        return expenseRepository.findTop20ByOrderByExpenseDateDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExpenseDTO createExpense(CreateExpenseRequest request, String registeredBy) {
        Expense expense = Expense.builder()
                .category(request.getCategory())
                .description(request.getDescription())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "EFECTIVO")
                .registeredBy(registeredBy)
                .build();

        expenseRepository.save(expense);
        return mapToDTO(expense);
    }

    public ExpenseDTO mapToDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .category(expense.getCategory())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .expenseDate(expense.getExpenseDate())
                .paymentMethod(expense.getPaymentMethod())
                .registeredBy(expense.getRegisteredBy())
                .build();
    }
}
