package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ReelUsageHistoryDTO {
    private Long id;
    private long reelNo;
    private String barcodeId;
    private double usedWeight;
    private ZonedDateTime usedAt;
    private String usedBy;
    private String reelSet;
    private String boxDetails;

}