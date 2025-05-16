package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelRegistrationResponseDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.ReelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/admin")
public class ReelController {

    @Autowired
    private ReelService reelService;

    @Autowired
    private ReelRepository reelRepository;

    @PostMapping("/register-reel")
    public ResponseEntity<ReelRegistrationResponseDTO> reelRegister(@RequestBody ReelDTO reeldata) {
        try {
            ReelRegistrationResponseDTO response = reelService.registerReel(reeldata);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // or return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/barcode/{barcodeId}")
    public ResponseEntity<byte[]> getBarcodeImage(@PathVariable String barcodeId) {
        Optional<Reel> reelOpt = Optional.ofNullable(reelRepository.findByBarcodeId(barcodeId));
        if (reelOpt.isPresent()) {
            Reel reel = reelOpt.get();
            byte[] image = reel.getBarcodeImage();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

//    @GetMapping("/reel/barcode-image/{id}")
//    public ResponseEntity<?> getBarcodeImage(@PathVariable String id) {
//        Reel reel = reelRepository.findByBarcodeId(id);
//
//        if (reel == null) {
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body("Reel with barcode ID '" + id + "' not found");
//        }
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_PNG)
//                .body(reel.getBarcodeImage());
//    }
}
