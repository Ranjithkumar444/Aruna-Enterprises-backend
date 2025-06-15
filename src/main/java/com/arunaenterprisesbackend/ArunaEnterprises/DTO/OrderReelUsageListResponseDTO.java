package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReelUsageListResponseDTO {
    private String barcodeId;
    private List<OrderReelUsageResponseDTO> usages;
}