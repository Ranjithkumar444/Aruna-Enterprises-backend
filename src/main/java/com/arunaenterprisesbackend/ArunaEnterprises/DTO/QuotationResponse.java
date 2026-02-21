package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuotationResponse {

    private double boxPrice;
    private double totalWeight;
    private double costPerSqm;
}