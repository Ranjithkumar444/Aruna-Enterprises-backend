package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double monthlyBaseSalary;
    private double otRatePerHour;

    private double totalSalaryThisMonth;

    private int month;

    private int year;

    private double totalOvertimeHours;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private int workingDays;

    private double otMultiplierFactor;

    private double otPerHour;

    private double oneDaySalary;

    private double oneHourSalary;

    private int daysWorked;
}

