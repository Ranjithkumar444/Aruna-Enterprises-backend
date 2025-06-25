package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStatus;
import lombok.Data;

@Data
public class ReelManipulationRequest {
    private String reelNoOrBarcodeId;
    private int initialWeight;
    private int currentWeight;
    private Integer previousWeight;
    private String unit;
    private ReelStatus status;
}