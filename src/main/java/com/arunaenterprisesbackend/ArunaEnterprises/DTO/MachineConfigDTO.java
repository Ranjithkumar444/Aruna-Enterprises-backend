package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MachineConfigDTO {
    private String machineCode;
    private String machineName;
    private String unit;
    private double maxCuttingLength;
    private double minCuttingLength;
    private double maxDeckle;
    private double minDeckle;
    private double noOfSheetsPerHour;
    private double noOfBoxPerHour;
}
