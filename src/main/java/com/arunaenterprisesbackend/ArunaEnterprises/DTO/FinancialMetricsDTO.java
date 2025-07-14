package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FinancialMetricsDTO {
    private double totalRevenue;
    private double totalProfit;
    private double profitMargin; // percentage
    private List<DailyFinancialData> dailyData;

    @Data
    @AllArgsConstructor
    public static class DailyFinancialData {
        private LocalDate date;
        private double revenue;
        private double profit;
    }
}
