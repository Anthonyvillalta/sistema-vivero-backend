package com.vivero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.vivero.dto.DashboardDTOs.*;
import com.vivero.dto.OrderDTOs.OrderDTO;
import com.vivero.dto.ProductDTOs.ProductDTO;
import com.vivero.entity.OrderStatus;
import com.vivero.repository.ExpenseRepository;
import com.vivero.repository.SaleRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SaleRepository saleRepository;
    private final ExpenseRepository expenseRepository;
    private final ProductService productService;
    private final OrderService orderService;

    public DashboardMetricsDTO getExecutiveMetrics() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);

        LocalDateTime yesterdayStart = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime yesterdayEnd = LocalDate.now().minusDays(1).atTime(LocalTime.MAX);

        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = LocalDate.now().atTime(LocalTime.MAX);

        LocalDateTime prevMonthStart = LocalDate.now().minusMonths(1).withDayOfMonth(1).atStartOfDay();
        LocalDateTime prevMonthEnd = LocalDate.now().withDayOfMonth(1).atStartOfDay().minusNanos(1);

        // 1. Real Daily Sales from DB
        BigDecimal dailySales = saleRepository.sumTotalByDateRange(todayStart, todayEnd);
        if (dailySales == null) dailySales = BigDecimal.ZERO;

        BigDecimal yesterdaySales = saleRepository.sumTotalByDateRange(yesterdayStart, yesterdayEnd);
        if (yesterdaySales == null) yesterdaySales = BigDecimal.ZERO;

        Double dailySalesGrowth = 0.0;
        if (yesterdaySales.compareTo(BigDecimal.ZERO) > 0) {
            dailySalesGrowth = dailySales.subtract(yesterdaySales)
                    .divide(yesterdaySales, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        } else if (dailySales.compareTo(BigDecimal.ZERO) > 0) {
            dailySalesGrowth = 100.0;
        }

        // 2. Real Monthly Sales from DB
        BigDecimal monthlySales = saleRepository.sumTotalByDateRange(monthStart, monthEnd);
        if (monthlySales == null) monthlySales = BigDecimal.ZERO;

        BigDecimal prevMonthSales = saleRepository.sumTotalByDateRange(prevMonthStart, prevMonthEnd);
        if (prevMonthSales == null) prevMonthSales = BigDecimal.ZERO;

        Double monthlySalesGrowth = 0.0;
        if (prevMonthSales.compareTo(BigDecimal.ZERO) > 0) {
            monthlySalesGrowth = monthlySales.subtract(prevMonthSales)
                    .divide(prevMonthSales, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        } else if (monthlySales.compareTo(BigDecimal.ZERO) > 0) {
            monthlySalesGrowth = 100.0;
        }

        // 3. Real Monthly Expenses from DB
        BigDecimal totalExpenses = expenseRepository.sumTotalByDateRange(monthStart, monthEnd);
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        BigDecimal prevMonthExpenses = expenseRepository.sumTotalByDateRange(prevMonthStart, prevMonthEnd);
        if (prevMonthExpenses == null) prevMonthExpenses = BigDecimal.ZERO;

        // 4. Real Net Profit = Monthly Sales - Monthly Expenses
        BigDecimal netProfit = monthlySales.subtract(totalExpenses);
        BigDecimal prevNetProfit = prevMonthSales.subtract(prevMonthExpenses);

        Double netProfitGrowth = 0.0;
        if (prevNetProfit.compareTo(BigDecimal.ZERO) > 0) {
            netProfitGrowth = netProfit.subtract(prevNetProfit)
                    .divide(prevNetProfit, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        } else if (netProfit.compareTo(BigDecimal.ZERO) > 0) {
            netProfitGrowth = 100.0;
        }

        // 5. Real 7-day Sales Chart calculated day by day from DB
        List<SalesChartData> salesChart = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM");
        for (int i = 6; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = d.atStartOfDay();
            LocalDateTime dayEnd = d.atTime(LocalTime.MAX);

            BigDecimal dayTotal = saleRepository.sumTotalByDateRange(dayStart, dayEnd);
            if (dayTotal == null) dayTotal = BigDecimal.ZERO;

            salesChart.add(SalesChartData.builder()
                    .date(d.format(formatter))
                    .amount(dayTotal)
                    .build());
        }

        // 6. Real Critical Stock Products
        List<ProductDTO> criticalStockProducts = productService.getCriticalStockProducts();

        // 7. Real Pending Orders
        List<OrderDTO> pendingOrders = orderService.getOrdersByStatus(OrderStatus.PENDIENTE);
        List<OrderDTO> preparingOrders = orderService.getOrdersByStatus(OrderStatus.PREPARANDO);
        List<OrderDTO> allPendingOrders = new ArrayList<>(pendingOrders);
        allPendingOrders.addAll(preparingOrders);

        // 8. Real Upcoming Deliveries (in delivery + pending/preparing)
        List<OrderDTO> upcomingDeliveries = new ArrayList<>(orderService.getOrdersByStatus(OrderStatus.EN_DELIVERY));
        for (OrderDTO pending : allPendingOrders) {
            if (upcomingDeliveries.stream().noneMatch(o -> o.getId().equals(pending.getId()))) {
                upcomingDeliveries.add(pending);
            }
        }

        // 9. Real Top Products (from catalog)
        List<ProductDTO> allProducts = productService.getAllProducts();
        List<TopProductDTO> topProducts = new ArrayList<>();
        for (int i = 0; i < Math.min(5, allProducts.size()); i++) {
            ProductDTO p = allProducts.get(i);
            topProducts.add(TopProductDTO.builder()
                    .id(p.getId())
                    .name(p.getName())
                    .variety(p.getVariety() != null ? p.getVariety() : p.getUnitType().toString())
                    .quantitySold(p.getStock())
                    .imageUrl(p.getImageUrl())
                    .build());
        }

        return DashboardMetricsDTO.builder()
                .dailySales(dailySales)
                .dailySalesGrowth(dailySalesGrowth)
                .monthlySales(monthlySales)
                .monthlySalesGrowth(monthlySalesGrowth)
                .netProfit(netProfit)
                .netProfitGrowth(netProfitGrowth)
                .totalExpenses(totalExpenses)
                .pendingOrdersCount(allPendingOrders.size())
                .criticalStockCount(criticalStockProducts.size())
                .salesChart(salesChart)
                .topProducts(topProducts)
                .criticalStockProducts(criticalStockProducts)
                .pendingOrders(allPendingOrders)
                .upcomingDeliveries(upcomingDeliveries)
                .build();
    }
}
