package com.arunaenterprisesbackend.ArunaEnterprises.DTO;


import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Salary;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalaryResponseDTO {
    private Long employeeId;
    private String name;
    private String barcodeId;
    private double totalSalaryThisMonth;
    private double totalOvertimeHours;
    private int month;
    private int year;
    private int daysWorked;

    public SalaryResponseDTO(Salary salary) {
    }
}

