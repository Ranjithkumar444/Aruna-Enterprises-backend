package com.arunaenterprisesbackend.ArunaEnterprises.DTO;


import lombok.Data;

import java.util.List;

@Data
public class OrderSummaryDetailDTO {
    private Long orderId;
    private String client;
    private String productType;
    private int quantity;
    private String size;
    private double topWeightConsumed;
    private double linerWeightConsumed;
    private double fluteWeightConsumed;
    private double totalWeightConsumed;
    private List<ReelUsageSummaryDTO> reelUsages;
    private String unit;
    private double profit;
    private String profitPercentage;
    private double revenue;
    private String revenuePercentage;
    private double totalReelWastage;
    private String totalReelWastagePercentage;
}
