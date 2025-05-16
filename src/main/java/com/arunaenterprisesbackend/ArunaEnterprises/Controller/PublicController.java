package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.Barcode;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.BarcodeDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.CalculationDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ContactDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Industry;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelUsageHistory;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.AttendanceService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.ContactService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.WeightCalculation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

   @Autowired
   private ReelUsageHistoryRepository reelUsageHistoryRepository;

   @Autowired
   private WeightCalculation weightCalculation;

   @Autowired
   private ReelRepository reelRepository;

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

    @PostMapping("/inventory/startedUsingReel")
    public ResponseEntity<String> reelStatusSet(@RequestBody BarcodeDTO barcodeDTO) {
        try {
            String barcodeId = barcodeDTO.getBarcodeId().trim();

            System.out.println("Received barcodeId: '" + barcodeId + "'");

            Reel reel = reelRepository.findByBarcodeId(barcodeId);

            if (reel == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reel not found for barcode ID");
            }
            if (reel.getStatus() == ReelStatus.USE_COMPLETED) {
                return ResponseEntity.badRequest().body("Reel is Completed its use");
            }

            reel.setReelSet(barcodeDTO.getReelSet());

            reel.setStatus(ReelStatus.IN_USE);
            reelRepository.save(reel);

            return ResponseEntity.ok("Reel status set to InUse");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }


    @PostMapping("/inventory/reelWeightCalculation")
    public ResponseEntity<String> calculateWeight(@RequestBody CalculationDTO calculationDTO) {
        try {
            Reel reel = reelRepository.findByBarcodeId(calculationDTO.getBarcodeId());

            if (reel == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reel not found for barcode ID");
            }

            if (reel.getStatus() != ReelStatus.IN_USE) {
                return ResponseEntity.badRequest().body("The reel is not set to use before");
            }

            String reelSet = reel.getReelSet(); // "flute" or "liner"

            double usedWeightGrams;

            usedWeightGrams = weightCalculation.calculateWeight(calculationDTO,reelSet);

            double usedWeightKg = usedWeightGrams / 1000.0;

            int currentWeight = (int) (reel.getCurrentWeight() - usedWeightKg);
            reel.setCurrentWeight(currentWeight);
            reelRepository.save(reel);

            return ResponseEntity.ok("Used Weight = " + usedWeightKg + " kg, Current Reel Weight = " + currentWeight + " kg");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

}
