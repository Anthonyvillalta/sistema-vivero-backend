package com.vivero.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDTOs {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DashboardMetricsDTO {
        private BigDecimal dailySales;
        private Double dailySalesGrowth; // e.g. +12.5% vs ayer
        private BigDecimal monthlySales;
        private Double monthlySalesGrowth; // e.g. +18.3% vs mes pasado
        private BigDecimal netProfit;
        private Double netProfitGrowth; // e.g. +15.7% vs mes pasado
        private BigDecimal totalExpenses;
        private Integer pendingOrdersCount;
        private Integer criticalStockCount;

        private List<SalesChartData> salesChart;
        private List<TopProductDTO> topProducts;
        private List<ProductDTOs.ProductDTO> criticalStockProducts;
        private List<OrderDTOs.OrderDTO> pendingOrders;
        private List<OrderDTOs.OrderDTO> upcomingDeliveries;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalesChartData {
        private String date; // e.g. '18 Jul', '19 Jul'
        private BigDecimal amount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopProductDTO {
        private Long id;
        private String name;
        private String variety;
        private String unitType;
        private BigDecimal quantitySold;
        private String imageUrl;
    }
}
