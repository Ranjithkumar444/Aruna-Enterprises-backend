package com.arunaenterprisesbackend.ArunaEnterprises.Controller;


import com.arunaenterprisesbackend.ArunaEnterprises.DTO.IndustryDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Industry;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.IndustryRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.IndustryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class IndustryController {

    @Autowired
    private IndustryService industryService;

    @Autowired
    private IndustryRepository industryRepository;

    @PostMapping("/register-industry")
    public ResponseEntity<String> registerIndustry(@Valid @RequestBody IndustryDTO industryDTO) {
        try {
            // Attempt to register the industry
            String result = industryService.registerIndustry(industryDTO);

            // Check the result
            if ("Industry registered successfully.".equals(result)) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to register industry: " + e.getMessage());
        }
    }

    @GetMapping("/get-industry/{id}")
    public ResponseEntity<byte[]> getIndustryBarcodeImage(@PathVariable Long id) {
        Industry employee = industryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(employee.getIndustryImage());
    }
}
