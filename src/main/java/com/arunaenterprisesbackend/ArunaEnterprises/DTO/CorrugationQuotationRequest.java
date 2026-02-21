package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorrugationQuotationRequest {

    private int length;
    private int width;
    private int height;

    private int ply;

    private List<PaperLayerRequest> layers;

    private int conversionCost;
}