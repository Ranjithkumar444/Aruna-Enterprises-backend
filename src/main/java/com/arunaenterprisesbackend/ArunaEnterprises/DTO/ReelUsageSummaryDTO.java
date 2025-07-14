package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.Data;

@Data
public class ReelUsageSummaryDTO {
    private Long reelNo;
    private String barcodeId;
    private String reelSet;
    private double weightConsumed;
    private String usageType;

    private String reelWastagePercentage;
    private double reelWastage;
}