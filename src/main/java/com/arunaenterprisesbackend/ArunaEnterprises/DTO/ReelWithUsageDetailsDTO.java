package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReelWithUsageDetailsDTO {
    // Reel details
    private String barcodeId;
    private Long reelNo;
    private int initialWeight;
    private int currentWeight;
    private int previousWeight;
    private String usageType;
    private String paperType;
    private int gsm;
    private int burstFactor;
    private int deckle;

    // Order usage details
    private List<OrderUsageDetailsDTO> orderUsages;
}