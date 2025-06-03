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
    private String unit;
    private double monthlyBaseSalary;

    public SalaryResponseDTO(Salary salary) {
        this.employeeId = salary.getEmployee().getId();
        this.name = salary.getEmployee().getName();
        this.barcodeId = salary.getEmployee().getBarcodeId();
        this.totalSalaryThisMonth = salary.getTotalSalaryThisMonth();
        this.totalOvertimeHours = salary.getTotalOvertimeHours();
        this.month = salary.getMonth();
        this.year = salary.getYear();
        this.unit = salary.getEmployee().getUnit();
        this.monthlyBaseSalary = salary.getMonthlyBaseSalary();
    }
}
