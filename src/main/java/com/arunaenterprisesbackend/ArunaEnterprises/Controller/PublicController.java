package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.Barcode;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.CalculationDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Attendance;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.AttendanceStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AttendanceRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.AttendanceService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelRepository;

@RestController
@CrossOrigin("*")
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ReelRepository reelRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/greet")
    public String HelloController(){
        return "Hello World";
    }

    @PostMapping("/attendance-scan")
    public String attendanceScan(@RequestBody  Barcode barcodeId){
        System.out.println(barcodeId.getBarcodeId());
        return barcodeId.getBarcodeId();
    }

    @PostMapping("/scan-attendance")
    public ResponseEntity<String> scanAttendance(@RequestBody Barcode barcodeId) {
        try {
            String response = attendanceService.markAttendance(barcodeId.getBarcodeId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }


    @PostMapping("/scan-inventory")
    public ResponseEntity<String> scanInventory(@RequestBody Reel scannedReel) {
        try {
            String response = inventoryService.toggleReelStatus(scannedReel.getBarcodeId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }

    @PostMapping("/calculate-usage")
    public ResponseEntity<String> calculateBoxWeight(@RequestBody CalculationDTO dto) {
        try {
            String message = inventoryService.calculateAndReduceWeight(dto);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Unexpected error: " + e.getMessage());
        }
    }
}
