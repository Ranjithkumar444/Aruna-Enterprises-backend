package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReelStockSummaryDTO {
    private String unit;
    private int deckle;
    private int gsm;
    private int burstFactor;
    private String paperType;
    private Long reelCount;
}