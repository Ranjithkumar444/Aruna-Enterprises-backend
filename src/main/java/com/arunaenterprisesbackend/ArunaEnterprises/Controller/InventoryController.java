package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin")
public class InventoryController {

    @Autowired
    public InventoryService inventoryService;

    @Autowired
    public ReelRepository reelRepository;

    @PostMapping("/register-reel")
    public ResponseEntity<String> reelRegistery(@RequestBody  ReelDTO reeldata){
        try {
            String response = inventoryService.registerReel(reeldata);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to register reel: " + e.getMessage());
        }
    }

    @GetMapping("/reel/barcode-image/{barcodeid}")
    public ResponseEntity<byte[]> getBarcodeImage(@PathVariable String barcodeid) {
        Reel reel = reelRepository.findByBarcodeId(barcodeid);

        if (reel == null || reel.getBarcodeImage() == null) {
            return ResponseEntity.badRequest().body("Reel not found".getBytes());
        }

        System.out.println("Image bytes: " + reel.getBarcodeImage().length);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(reel.getBarcodeImage());
    }

}
