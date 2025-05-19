package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalaryRequestDTO {
    private Long employeeId;
    private double monthlyBaseSalary;
    private double otMultiplierFactor;
    private int workingDays;
}

