package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PunchingQuotationRequest {

    private int sheetLength;
    private int sheetWidth;

    private int dieLength;
    private int dieWidth;

    private List<PaperLayerRequest> layers;

    private int conversionCost;
}