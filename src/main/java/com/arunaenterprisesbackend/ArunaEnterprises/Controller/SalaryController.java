package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.SalaryRequestDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
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
}

