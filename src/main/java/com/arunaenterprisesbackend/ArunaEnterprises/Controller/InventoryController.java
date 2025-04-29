package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin")
public class InventoryController {

    @Autowired
    public InventoryService inventoryService;

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
}
