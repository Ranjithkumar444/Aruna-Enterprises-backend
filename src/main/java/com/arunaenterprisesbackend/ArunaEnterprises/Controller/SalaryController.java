package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.SalaryRequestDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.SalaryResponseDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Salary;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @PostMapping("/salary/add")
    public ResponseEntity<String> addSalary(@RequestBody SalaryRequestDTO request) {
        try {
            String response = salaryService.saveSalary(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save salary: " + e.getMessage());
        }
    }

    @GetMapping("salary/monthly")
    public ResponseEntity<List<SalaryResponseDTO>> getCurrentMonthSalary() {
        List<SalaryResponseDTO> salaryData = salaryService.getCurrentMonthSalaryForAllEmployees();
        return ResponseEntity.ok(salaryData);
    }

    @GetMapping("/api/salaries/current")
    public ResponseEntity<List<SalaryResponseDTO>> getCurrentSalaries() {
        return ResponseEntity.ok(salaryService.getCurrentMonthSalaryForAllEmployees());
    }

    @GetMapping("salary/latest")
    public ResponseEntity<List<Salary>> getLatestSalaries() {
        List<Salary> latestSalaries = salaryService.getLatestSalariesForAllEmployees();
        return ResponseEntity.ok(latestSalaries);
    }
}
