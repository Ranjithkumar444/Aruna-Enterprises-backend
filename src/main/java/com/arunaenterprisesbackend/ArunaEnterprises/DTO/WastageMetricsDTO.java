package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class WastageMetricsDTO {
    private double totalWastage;
    private double averageWastagePercent;
    private Map<String, Double> wastageByMaterialType;
    private List<DailyWastageData> dailyData;

    @Data
    @AllArgsConstructor
    public static class DailyWastageData {
        private LocalDate date;
        private double wastage;
        private double wastagePercent;
    }
}