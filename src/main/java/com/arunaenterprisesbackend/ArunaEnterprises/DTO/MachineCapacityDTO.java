package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MachineCapacityDTO {
    private String machineName;
    private String unit;
    private double maxCuttingLength;
    private double minCuttingLength;
    private double maxDeckle;
    private double minDeckle;
    private double noOfSheetsPerHour;
    private double noOfBoxPerHour;

    public MachineCapacityDTO(String machineName, double maxDeckle, double minDeckle, double maxCuttingLength, double minCuttingLength, double noOfBoxPerHour, double noOfSheetsPerHour) {
    }
}
