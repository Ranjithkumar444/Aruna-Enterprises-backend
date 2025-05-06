package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.ReelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/admin")
public class ReelController {

    @Autowired
    private ReelService reelService;

    @Autowired
    private ReelRepository reelRepository;

    @PostMapping("/register-reel")
    public ResponseEntity<String> reelRegister(@RequestBody  ReelDTO reeldata){
        try {
            String response = reelService.registerReel(reeldata);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to register reel: " + e.getMessage());
        }
    }

    @GetMapping("/reel/barcode-image/{id}")
    public ResponseEntity<byte[]> getBarcodeImage(@PathVariable Long id) {
        Reel reel = reelRepository.findById(id)
                .orElseThrow();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(reel.getBarcodeImage());
    }
}
