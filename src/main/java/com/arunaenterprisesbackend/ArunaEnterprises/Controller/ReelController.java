package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelDetailsDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelRegistrationResponseDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.ReelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
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
                    .body(null);
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

    @GetMapping("barcode/details/{barcodeId}")
    public ResponseEntity<ReelDetailsDTO> getReelDetails(@PathVariable String barcodeId) {
        Optional<Reel> optionalReel = Optional.ofNullable(reelRepository.findByBarcodeId(barcodeId));

        if (optionalReel.isPresent()) {
            Reel reel = optionalReel.get();

            ReelDetailsDTO dto = new ReelDetailsDTO();
            dto.setBurstFactor(optionalReel.get().getBurstFactor());
            dto.setGsm(optionalReel.get().getGsm());
            dto.setDeckle(optionalReel.get().getDeckle());
            dto.setSupplierName(optionalReel.get().getSupplierName());
            dto.setCurrentWeight(optionalReel.get().getCurrentWeight());

            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/reel/barcode-image/{id}")
    public ResponseEntity<?> getBarcodeimage(@PathVariable String id) {
        Reel reel = reelRepository.findByBarcodeId(id);

        if (reel == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Reel with barcode ID '" + id + "' not found");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(reel.getBarcodeImage());
    }

    @GetMapping("/inventory/getReelStocks")
    public ResponseEntity<List<Reel>> getReelStocks() {
        List<ReelStatus> statuses = Arrays.asList(ReelStatus.IN_USE, ReelStatus.NOT_IN_USE);
        List<Reel> reels = reelRepository.findByStatusIn(statuses);
        return ResponseEntity.ok(reels);
    }

}
