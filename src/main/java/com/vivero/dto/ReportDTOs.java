package com.vivero.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public class ReportDTOs {

    @Data
    @Builder
    public static class SalesSummaryDTO {
        private BigDecimal totalSales;
        private Long totalTransactions;
        private BigDecimal averageTicket;
        private BigDecimal yesterdaySales;
        private Double growthPercentage;
        private List<SalesByDateDTO> dailyTrend;
        private List<PaymentMethodBreakdownDTO> paymentBreakdown;
        private List<CategorySalesDTO> salesByCategory;
    }

    @Data
    @Builder
    public static class SalesByDateDTO {
        private String date;
        private BigDecimal amount;
        private Long count;
    }

    @Data
    @Builder
    public static class PaymentMethodBreakdownDTO {
        private String method;
        private BigDecimal amount;
        private Long count;
    }

    @Data
    @Builder
    public static class CategorySalesDTO {
        private Long categoryId;
        private String categoryName;
        private BigDecimal totalAmount;
        private Long totalQuantity;
        private BigDecimal averagePrice;
    }

    @Data
    @Builder
    public static class ProductSalesDTO {
        private Long productId;
        private String productName;
        private String variety;
        private String categoryName;
        private BigDecimal quantitySold;
        private BigDecimal totalRevenue;
        private BigDecimal totalCost;
        private BigDecimal grossProfit;
        private BigDecimal profitMargin;
    }

    @Data
    @Builder
    public static class ExpenseSummaryDTO {
        private BigDecimal totalExpenses;
        private Long totalTransactions;
        private Double averageExpense;
        private List<ExpenseByCategoryDTO> byCategory;
        private List<ExpenseByDateDTO> dailyTrend;
    }

    @Data
    @Builder
    public static class ExpenseByCategoryDTO {
        private String category;
        private BigDecimal amount;
        private Long count;
    }

    @Data
    @Builder
    public static class ExpenseByDateDTO {
        private String date;
        private BigDecimal amount;
    }

    @Data
    @Builder
    public static class PurchaseSummaryDTO {
        private BigDecimal totalPurchases;
        private Long totalTransactions;
        private BigDecimal averagePurchase;
        private List<SupplierSpendingDTO> topSuppliers;
        private List<PurchaseByDateDTO> dailyTrend;
    }

    @Data
    @Builder
    public static class SupplierSpendingDTO {
        private Long supplierId;
        private String supplierName;
        private BigDecimal amount;
        private Long purchaseCount;
    }

    @Data
    @Builder
    public static class PurchaseByDateDTO {
        private String date;
        private BigDecimal amount;
    }

    @Data
    @Builder
    public static class ProfitMarginDTO {
        private BigDecimal totalRevenue;
        private BigDecimal totalCostOfGoods;
        private BigDecimal grossProfit;
        private BigDecimal grossProfitMargin;
        private BigDecimal totalExpenses;
        private BigDecimal netProfit;
        private BigDecimal netProfitMargin;
        private List<ProductSalesDTO> topProductsByProfit;
        private List<CategorySalesDTO> categoryMargins;
    }

    @Data
    @Builder
    public static class InventoryValuationDTO {
        private BigDecimal totalStockValue;
        private Long totalProducts;
        private Long lowStockCount;
        private Long outOfStockCount;
        private List<StockCategoryDTO> valuationByCategory;
        List<StockProductDTO> lowStockProducts;
    }

    @Data
    @Builder
    public static class StockCategoryDTO {
        private Long categoryId;
        private String categoryName;
        private BigDecimal totalValue;
        private Long totalQuantity;
    }

    @Data
    @Builder
    public static class StockProductDTO {
        private Long productId;
        private String productName;
        private String categoryName;
        private String unitType;
        private BigDecimal stock;
        private BigDecimal costPrice;
        private BigDecimal stockValue;
        private BigDecimal minStock;
    }

    @Data
    @Builder
    public static class TopCustomerDTO {
        private Long customerId;
        private String fullName;
        private String phone;
        private BigDecimal totalPurchases;
        private Long purchaseCount;
        private Double customerLifetimeValue;
    }

    @Data
    @Builder
    public static class InventoryMovementReportDTO {
        private Long movementId;
        private Long productId;
        private String productName;
        private String categoryName;
        private String movementType;
        private BigDecimal quantity;
        private BigDecimal previousStock;
        private BigDecimal newStock;
        private BigDecimal unitCost;
        private BigDecimal movementValue;
        private String reason;
        private String referenceId;
        private String createdAt;
        private String createdBy;
    }
}
