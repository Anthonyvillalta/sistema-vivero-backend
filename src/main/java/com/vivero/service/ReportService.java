package com.vivero.service;

import com.vivero.dto.ReportDTOs.*;
import com.vivero.entity.*;
import com.vivero.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final SaleRepository saleRepository;
    private final ExpenseRepository expenseRepository;
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final CustomerRepository customerRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM");
    private static final DateTimeFormatter FULL_DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private LocalDateTime[] resolveDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime end = (endDate != null) ? endDate.atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);
        return new LocalDateTime[]{start, end};
    }

    public SalesSummaryDTO getSalesSummary(LocalDate startDate, LocalDate endDate) {
        LocalDateTime[] range = resolveDateRange(startDate, endDate);
        LocalDateTime start = range[0];
        LocalDateTime end = range[1];

        BigDecimal totalSales = saleRepository.sumTotalByDateRange(start, end);
        Long totalTransactions = saleRepository.countByDateRange(start, end);
        BigDecimal averageTicket = totalTransactions > 0
                ? totalSales.divide(BigDecimal.valueOf(totalTransactions), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        LocalDateTime yesterdayStart = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime yesterdayEnd = LocalDate.now().minusDays(1).atTime(LocalTime.MAX);
        BigDecimal yesterdaySales = saleRepository.sumTotalByDateRange(yesterdayStart, yesterdayEnd);

        Double growth = 0.0;
        if (yesterdaySales.compareTo(BigDecimal.ZERO) > 0) {
            growth = totalSales.subtract(yesterdaySales)
                    .divide(yesterdaySales, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        List<SalesByDateDTO> dailyTrend = saleRepository.dailySalesByDateRange(start, end)
                .stream()
                .map(r -> SalesByDateDTO.builder()
                        .date((String) r[0])
                        .amount((BigDecimal) r[1])
                        .count(0L)
                        .build())
                .collect(Collectors.toList());

        List<PaymentMethodBreakdownDTO> paymentBreakdown = saleRepository.sumByPaymentMethod(start, end)
                .stream()
                .map(r -> PaymentMethodBreakdownDTO.builder()
                        .method((String) r[0])
                        .amount((BigDecimal) r[1])
                        .count((Long) r[2])
                        .build())
                .collect(Collectors.toList());

        List<CategorySalesDTO> salesByCategory = saleRepository.sumSalesByCategory(start, end)
                .stream()
                .map(r -> CategorySalesDTO.builder()
                        .categoryId((Long) r[0])
                        .categoryName((String) r[1])
                        .totalAmount((BigDecimal) r[2])
                        .totalQuantity(((Number) r[3]).longValue())
                        .build())
                .collect(Collectors.toList());

        if (!salesByCategory.isEmpty()) {
            for (CategorySalesDTO cat : salesByCategory) {
                if (cat.getTotalQuantity() > 0) {
                    cat.setAveragePrice(
                            cat.getTotalAmount().divide(BigDecimal.valueOf(cat.getTotalQuantity()), 2, RoundingMode.HALF_UP)
                    );
                }
            }
        }

        return SalesSummaryDTO.builder()
                .totalSales(totalSales)
                .totalTransactions(totalTransactions)
                .averageTicket(averageTicket)
                .yesterdaySales(yesterdaySales)
                .growthPercentage(growth)
                .dailyTrend(dailyTrend)
                .paymentBreakdown(paymentBreakdown)
                .salesByCategory(salesByCategory)
                .build();
    }

    public List<ProductSalesDTO> getProductSalesRanking(LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime[] range = resolveDateRange(startDate, endDate);
        List<Object[]> results = productRepository.findTopProductsBySales(range[0], range[1]);

        return results.stream()
                .limit(limit)
                .map(r -> {
                    BigDecimal revenue = (BigDecimal) r[5];
                    BigDecimal qty = (BigDecimal) r[4];
                    BigDecimal cost = BigDecimal.ZERO;
                    BigDecimal grossProfit = revenue.subtract(cost);
                    BigDecimal margin = revenue.compareTo(BigDecimal.ZERO) > 0
                            ? grossProfit.divide(revenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                            : BigDecimal.ZERO;
                    return ProductSalesDTO.builder()
                            .productId((Long) r[0])
                            .productName((String) r[1])
                            .variety((String) r[2])
                            .categoryName((String) r[3])
                            .quantitySold(qty)
                            .totalRevenue(revenue)
                            .totalCost(cost)
                            .grossProfit(grossProfit)
                            .profitMargin(margin)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public ExpenseSummaryDTO getExpenseSummary(LocalDate startDate, LocalDate endDate) {
        LocalDateTime[] range = resolveDateRange(startDate, endDate);
        LocalDateTime start = range[0];
        LocalDateTime end = range[1];

        BigDecimal totalExpenses = expenseRepository.sumTotalByDateRange(start, end);
        Long totalTransactions = expenseRepository.countByDateRange(start, end);
        Double averageExpense = totalTransactions > 0
                ? totalExpenses.divide(BigDecimal.valueOf(totalTransactions), 2, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        List<ExpenseByCategoryDTO> byCategory = expenseRepository.sumByCategory(start, end)
                .stream()
                .map(r -> ExpenseByCategoryDTO.builder()
                        .category((String) r[0])
                        .amount((BigDecimal) r[1])
                        .count((Long) r[2])
                        .build())
                .collect(Collectors.toList());

        List<ExpenseByDateDTO> dailyTrend = expenseRepository.dailyExpensesByDateRange(start, end)
                .stream()
                .map(r -> ExpenseByDateDTO.builder()
                        .date((String) r[0])
                        .amount((BigDecimal) r[1])
                        .build())
                .collect(Collectors.toList());

        return ExpenseSummaryDTO.builder()
                .totalExpenses(totalExpenses)
                .totalTransactions(totalTransactions)
                .averageExpense(averageExpense)
                .byCategory(byCategory)
                .dailyTrend(dailyTrend)
                .build();
    }

    public PurchaseSummaryDTO getPurchaseSummary(LocalDate startDate, LocalDate endDate) {
        LocalDateTime[] range = resolveDateRange(startDate, endDate);
        LocalDateTime start = range[0];
        LocalDateTime end = range[1];

        BigDecimal totalPurchases = purchaseRepository.sumTotalByDateRange(start, end);
        Long totalTransactions = purchaseRepository.countByDateRange(start, end);
        BigDecimal averagePurchase = totalTransactions > 0
                ? totalPurchases.divide(BigDecimal.valueOf(totalTransactions), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<SupplierSpendingDTO> topSuppliers = purchaseRepository.sumBySupplier(start, end)
                .stream()
                .map(r -> SupplierSpendingDTO.builder()
                        .supplierId((Long) r[0])
                        .supplierName((String) r[1])
                        .amount((BigDecimal) r[2])
                        .purchaseCount((Long) r[3])
                        .build())
                .collect(Collectors.toList());

        List<PurchaseByDateDTO> dailyTrend = purchaseRepository.dailyPurchasesByDateRange(start, end)
                .stream()
                .map(r -> PurchaseByDateDTO.builder()
                        .date((String) r[0])
                        .amount((BigDecimal) r[1])
                        .build())
                .collect(Collectors.toList());

        return PurchaseSummaryDTO.builder()
                .totalPurchases(totalPurchases)
                .totalTransactions(totalTransactions)
                .averagePurchase(averagePurchase)
                .topSuppliers(topSuppliers)
                .dailyTrend(dailyTrend)
                .build();
    }

    public ProfitMarginDTO getProfitMargin(LocalDate startDate, LocalDate endDate) {
        LocalDateTime[] range = resolveDateRange(startDate, endDate);
        LocalDateTime start = range[0];
        LocalDateTime end = range[1];

        BigDecimal totalRevenue = saleRepository.sumTotalByDateRange(start, end);
        BigDecimal totalExpenses = expenseRepository.sumTotalByDateRange(start, end);

        List<ProductSalesDTO> productSales = getProductSalesRanking(startDate, endDate, 10);

        BigDecimal totalCostOfGoods = productSales.stream()
                .map(ProductSalesDTO::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalCostOfGoods.compareTo(BigDecimal.ZERO) == 0) {
            totalCostOfGoods = totalRevenue.multiply(BigDecimal.valueOf(0.4));
        }

        BigDecimal grossProfit = totalRevenue.subtract(totalCostOfGoods);
        BigDecimal grossProfitMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? grossProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        BigDecimal netProfit = grossProfit.subtract(totalExpenses);
        BigDecimal netProfitMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? netProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        List<CategorySalesDTO> categoryMargins = saleRepository.sumSalesByCategory(start, end)
                .stream()
                .map(r -> CategorySalesDTO.builder()
                        .categoryId((Long) r[0])
                        .categoryName((String) r[1])
                        .totalAmount((BigDecimal) r[2])
                        .totalQuantity(((Number) r[3]).longValue())
                        .build())
                .collect(Collectors.toList());

        return ProfitMarginDTO.builder()
                .totalRevenue(totalRevenue)
                .totalCostOfGoods(totalCostOfGoods)
                .grossProfit(grossProfit)
                .grossProfitMargin(grossProfitMargin)
                .totalExpenses(totalExpenses)
                .netProfit(netProfit)
                .netProfitMargin(netProfitMargin)
                .topProductsByProfit(productSales)
                .categoryMargins(categoryMargins)
                .build();
    }

    public InventoryValuationDTO getInventoryValuation() {
        List<Product> products = productRepository.findAllWithCategory();

        List<StockProductDTO> allStockProducts = products.stream()
                .filter(p -> p.getStock() != null && p.getStock().compareTo(BigDecimal.ZERO) > 0)
                .map(p -> StockProductDTO.builder()
                        .productId(p.getId())
                        .productName(p.getName())
                        .categoryName(p.getCategory() != null ? p.getCategory().getName() : "Sin categoría")
                        .unitType(p.getUnitType().toString())
                        .stock(p.getStock())
                        .costPrice(p.getCostPrice())
                        .stockValue(p.getStock().multiply(p.getCostPrice()))
                        .minStock(p.getMinStock())
                        .build())
                .collect(Collectors.toList());

        BigDecimal totalStockValue = allStockProducts.stream()
                .map(StockProductDTO::getStockValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<StockCategoryDTO> valuationByCategory = productRepository.inventoryValuationByCategory()
                .stream()
                .map(r -> StockCategoryDTO.builder()
                        .categoryId((Long) r[0])
                        .categoryName((String) r[1])
                        .totalValue((BigDecimal) r[2])
                        .totalQuantity(((Number) r[3]).longValue())
                        .build())
                .collect(Collectors.toList());

        List<StockProductDTO> lowStock = products.stream()
                .filter(p -> p.getActive() && p.getStock() != null && p.getStock().compareTo(p.getMinStock()) <= 0)
                .map(p -> StockProductDTO.builder()
                        .productId(p.getId())
                        .productName(p.getName())
                        .categoryName(p.getCategory() != null ? p.getCategory().getName() : "Sin categoría")
                        .unitType(p.getUnitType().toString())
                        .stock(p.getStock())
                        .costPrice(p.getCostPrice())
                        .stockValue(p.getStock().multiply(p.getCostPrice()))
                        .minStock(p.getMinStock())
                        .build())
                .sorted((a, b) -> Double.compare(
                        b.getStock().doubleValue() / b.getMinStock().doubleValue(),
                        a.getStock().doubleValue() / a.getMinStock().doubleValue()))
                .collect(Collectors.toList());

        long outOfStock = products.stream().filter(p -> p.getStock() != null && p.getStock().compareTo(BigDecimal.ZERO) == 0).count();

        return InventoryValuationDTO.builder()
                .totalStockValue(totalStockValue)
                .totalProducts((long) allStockProducts.size())
                .lowStockCount((long) lowStock.size())
                .outOfStockCount(outOfStock)
                .valuationByCategory(valuationByCategory)
                .lowStockProducts(lowStock)
                .build();
    }

    public List<TopCustomerDTO> getTopCustomers(LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime[] range = resolveDateRange(startDate, endDate);
        List<Object[]> results = customerRepository.findTopCustomersBySales(range[0], range[1]);

        return results.stream()
                .limit(limit)
                .map(r -> {
                    BigDecimal total = (BigDecimal) r[4];
                    Long count = (Long) r[3];
                    Double avg = count > 0 ? total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP).doubleValue() : 0.0;
                    return TopCustomerDTO.builder()
                            .customerId((Long) r[0])
                            .fullName((String) r[1])
                            .phone((String) r[2])
                            .purchaseCount(count)
                            .totalPurchases(total)
                            .customerLifetimeValue(avg)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
