package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class DailyOrderSummaryDTO {
    private Long id;
    private LocalDate summaryDate;
    private int totalOrdersShipped;
    private double totalWeightConsumed;
    private ZonedDateTime createdAt;
    private List<OrderSummaryDetailDTO> orderDetails;

    private double totalProfitOfDay;
    private double totalRevenueOfDay;
    private String totalProfitOfDayPercentage;
    private String totalRevenueOfDayPercentage;
    private double totalReelWastageOfDay;
    private String totalReelWastageOfDayPercentage;
}