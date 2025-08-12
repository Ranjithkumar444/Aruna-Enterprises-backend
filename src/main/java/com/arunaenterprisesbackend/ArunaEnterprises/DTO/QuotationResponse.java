package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotationResponse {

    private String boardSizeRequiredPerBox;
    private double actualBoardAreaUsedPerBoxSqm;

    private double totalGSM;
    private double boxWeightKg;

    private double materialCostPerBox;
    private double conversionCostPerBox;
    private double totalCostBeforeMarginPerBox;
    private double unitPrice;

    private String calculationNotes;
}