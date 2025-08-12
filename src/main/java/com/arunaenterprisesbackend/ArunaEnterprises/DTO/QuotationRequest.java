package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotationRequest {

    private String boxType; // e.g., "Corrugated RSC", "Die-Cut"

    private double length;
    private double width;
    private double height;

    private double trimAllowanceLength;
    private double trimAllowanceWidth;

    // Use a list to represent multiple layers
    private List<LayerDetails> layers;

    private double conversionCostPerKg;
    private double processWastePercentage;
    private double grossMarginPercentage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LayerDetails {
        private String layerType; // e.g., "Top", "Flute", "Liner"
        private String shade;
        private double gsm;
        private double bf; // BF is now used in the calculation
        private double pricePerKg;
        private String fluteType; // A, B, C, E, etc.
    }
}