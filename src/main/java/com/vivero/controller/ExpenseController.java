package com.vivero.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.dto.ExpenseDTOs.*;
import com.vivero.service.ExpenseService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
@Tag(name = "Gastos Operativos", description = "Control de Gastos (Transporte, Personal, Insumos, Mantenimiento)")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    @Operation(summary = "Listar gastos recientes")
    public ResponseEntity<List<ExpenseDTO>> getRecentExpenses() {
        return ResponseEntity.ok(expenseService.getRecentExpenses());
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo gasto operativo")
    public ResponseEntity<ExpenseDTO> createExpense(@Valid @RequestBody CreateExpenseRequest request, Principal principal) {
        String username = principal != null ? principal.getName() : "admin";
        return ResponseEntity.ok(expenseService.createExpense(request, username));
    }
}
