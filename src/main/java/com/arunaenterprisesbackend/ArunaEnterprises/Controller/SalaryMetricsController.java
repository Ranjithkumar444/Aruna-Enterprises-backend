package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.SalaryMetricsDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.SalaryMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/salary/metrics")
public class SalaryMetricsController {

    @Autowired
    private SalaryMetricsService salaryMetricsService;

    @GetMapping("/unit-wise")
    public ResponseEntity<List<SalaryMetricsDTO>> getUnitWiseSalaryMetrics() {
        return ResponseEntity.ok(salaryMetricsService.getUnitWiseMetrics());
    }

    @GetMapping("/employee-wise")
    public ResponseEntity<List<SalaryMetricsDTO>> getEmployeeWiseSalaryMetrics() {
        return ResponseEntity.ok(salaryMetricsService.getEmployeeWiseMetrics());
    }

    @GetMapping("/metrics/unit")
    public ResponseEntity<List<SalaryMetricsDTO>> getUnitWiseSalary(
            @RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(salaryMetricsService.getUnitWiseSalaryByMonth(month, year));
    }

    @GetMapping("/metrics/employee")
    public ResponseEntity<List<SalaryMetricsDTO>> getEmployeeWiseSalary(
            @RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(salaryMetricsService.getEmployeeWiseSalaryByMonth(month, year));
    }
}
