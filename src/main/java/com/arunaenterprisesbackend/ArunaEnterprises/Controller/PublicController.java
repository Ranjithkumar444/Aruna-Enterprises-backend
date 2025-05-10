package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.Barcode;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ContactDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Industry;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AttendanceRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.IndustryRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.AttendanceService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private IndustryRepository industryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

   @Autowired
    private AttendanceService attendanceService;

   @Autowired
   private ContactService contactService;

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

    @PostMapping("/contact-details")
    public ResponseEntity<String> registerContactInfo(@RequestBody ContactDTO contactinfo){
        try{
            String response = contactService.registerContactInfo(contactinfo);
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getAllIndustry")
    public ResponseEntity<?> getAllIndustry() {
        try {
            List<Industry> industryList = industryRepository.findAll();
            return ResponseEntity.ok(industryList);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch industries: " + e.getMessage());
        }
    }

   }
