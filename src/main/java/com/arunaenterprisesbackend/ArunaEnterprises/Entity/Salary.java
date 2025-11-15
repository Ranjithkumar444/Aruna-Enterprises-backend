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

    private double monthlyBaseSalary; //base salary
    private double otRatePerHour;

    private double totalSalaryThisMonth;

    private int month;

    private int year;

    private double totalOvertimeHours;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private int workingDays; //26 or 30 days working

    private double regularHoursPerDay; // 26 - 8hours and 30 days workers 12 hours

    private double otMultiplierFactor; // ot rate like 1X or 1.5X

    private double otPerHour; // ot salary per hours

    private double oneDaySalary;

    private double oneHourSalary;
}
