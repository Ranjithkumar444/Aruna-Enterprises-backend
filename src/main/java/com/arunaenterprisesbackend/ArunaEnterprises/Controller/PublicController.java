package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.Barcode;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.BarcodeDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.CalculationDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ContactDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.AttendanceService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.WeightCalculation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
   private ReelUsageHistoryRepository reelUsageHistoryRepository;

   @Autowired
   private WeightCalculation weightCalculation;

   @Autowired
   private ReelRepository reelRepository;

   @Autowired
   private ContactRepository contactRepository;

   @Autowired
   private BoxRepository boxRepository;

   @Autowired
   private BoxDetailsRepository boxDetailsRepository;

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
    public ResponseEntity<String> handleContactForm(@RequestBody ContactDTO request) {
        try {
            ContactMessage contactMessage = new ContactMessage();

            contactMessage.setName(request.getName());
            contactMessage.setPhone(request.getPhone());
            contactMessage.setMessage(request.getMessage());
            contactMessage.setCreatedAt(LocalDateTime.now());
            contactMessage.setMessage(request.getMessage());

            contactRepository.save(contactMessage);

            return ResponseEntity.ok("Thank You for contacting us we will reach you soon");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error in Contacting Us");
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
            ReelUsageHistory reelUsageHistory = new ReelUsageHistory();

            if (reel == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reel not found for barcode ID");
            }

            if (reel.getStatus() != ReelStatus.IN_USE) {
                return ResponseEntity.badRequest().body("The reel is not set to use before");
            }

            String reelSet = reel.getReelSet();

            double usedWeightGrams;

            usedWeightGrams = weightCalculation.calculateWeight(calculationDTO,reelSet);

            double usedWeightKg = usedWeightGrams / 1000.0;

            int currentWeight = (int) (reel.getCurrentWeight() - usedWeightKg);
            reel.setCurrentWeight(currentWeight);
            if(currentWeight <= 10){
                reel.setStatus(ReelStatus.USE_COMPLETED);
            }
            else{
                reel.setStatus(ReelStatus.NOT_IN_USE);
            }

            reelUsageHistory.setBarcodeId(calculationDTO.getBarcodeId());
            reelUsageHistory.setReelSet(reel.getReelSet());
            reelUsageHistory.setUsedWeight(usedWeightKg);
            reelUsageHistory.setUsedAt(LocalDateTime.now());
            reelUsageHistory.setBoxDetails(
                    String.valueOf(calculationDTO.getLength() + " " + calculationDTO.getWidth() + " " + calculationDTO.getHeight())
            );
            reelUsageHistory.setUsedBy(calculationDTO.getScannedBy());

            reelUsageHistoryRepository.save(reelUsageHistory);

            reelRepository.save(reel);

            return ResponseEntity.ok("Used Weight = " + usedWeightKg + " kg, Current Reel Weight = " + currentWeight + " kg");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/box/getAllBoxDetails")
    public ResponseEntity<List<Box>> getAllDetilsOfBox(){
        try{
            List<Box> list = boxRepository.findAll();
            return ResponseEntity.ok(list);
        }
        catch (Exception e){
            List<Box> list = new ArrayList<>();
            return ResponseEntity.badRequest().body(list);
        }
    }

    @GetMapping("/box/getBoxDetails")
    public ResponseEntity<List<BoxDetails>> getAllBoxDetails(){
        try{
            List<BoxDetails> list = boxDetailsRepository.findAll();
            return ResponseEntity.ok(list);
        } catch(Exception e) {
            List<BoxDetails> list = null;
            return ResponseEntity.badRequest().body(list);
        }
    }

}
