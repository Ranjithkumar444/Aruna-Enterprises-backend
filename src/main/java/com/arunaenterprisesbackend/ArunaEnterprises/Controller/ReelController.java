package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderReelUsage;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderReelUsageRepository;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class ReelController {

    @Autowired
    private ReelService reelService;

    @Autowired
    private ReelRepository reelRepository;

    @Autowired
    private OrderReelUsageRepository orderReelUsageRepository;

    @PostMapping("/register-reel")
    public ResponseEntity<ReelRegistrationResponseDTO> reelRegister(@RequestBody ReelDTO reeldata) {
        System.out.println("Reel No received: " + reeldata.getReelNo());
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
            dto.setReelNo(optionalReel.get().getReelNo());
            dto.setSupplierName(optionalReel.get().getSupplierName());
            dto.setCurrentWeight(optionalReel.get().getCurrentWeight());

            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/reel/barcode-image/{id}")
    public ResponseEntity<?> getBarcodeimage(@PathVariable String id) {
        Reel reel;
        try {
            // Try to parse as Long first (for reelNo)
            Long reelNo = Long.valueOf(id);
            reel = reelRepository.findByReelNo(reelNo);
        } catch (NumberFormatException e) {
            // If not a number, search by barcodeId
            reel = reelRepository.findByBarcodeId(id);
        }

        if (reel == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Reel with ID '" + id + "' not found");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(reel.getBarcodeImage());
    }

    @GetMapping("/inventory/getReelStocks")
    public ResponseEntity<List<Reel>> getReelStocks() {
        List<ReelStatus> statuses = Arrays.asList(ReelStatus.IN_USE, ReelStatus.NOT_IN_USE,ReelStatus.PARTIALLY_USED_AVAILABLE);
        List<Reel> reels = reelRepository.findByStatusIn(statuses);
        return ResponseEntity.ok(reels);
    }

    @GetMapping("/reel/details/{id}")
    public ResponseEntity<ReelResponseDTO> getReelFullDetails(@PathVariable String id) {
        Reel reel;
        try {
            // Try to parse as Long first (for reelNo)
            Long reelNo = Long.valueOf(id);
            reel = reelRepository.findByReelNo(reelNo);
        } catch (NumberFormatException e) {
            // If not a number, search by barcodeId
            reel = reelRepository.findByBarcodeId(id);
        }

        if (reel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        ReelResponseDTO dto = new ReelResponseDTO();
        dto.setBarcodeId(reel.getBarcodeId());
        dto.setGsm(reel.getGsm());
        dto.setDeckle(reel.getDeckle());
        dto.setBurstFactor(reel.getBurstFactor());
        dto.setInitialWeight(reel.getInitialWeight());
        dto.setCurrentWeight(reel.getCurrentWeight());
        dto.setReelNo(reel.getReelNo());
        dto.setSupplierName(reel.getSupplierName());
        dto.setStatus(reel.getStatus());
        dto.setPaperType(reel.getPaperType());

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/reel/orderReelUsage/{barcodeId}")
    public ResponseEntity<?> getAllOrderReelUsageByBarcode(@PathVariable String barcodeId) {
        try {
            List<OrderReelUsage> usages = orderReelUsageRepository.findAllByReelBarcodeId(barcodeId);

            if (usages.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            OrderReelUsageListResponseDTO response = new OrderReelUsageListResponseDTO();
            response.setBarcodeId(barcodeId);
            response.setUsages(usages.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error fetching order reel usages: " + e.getMessage());
        }
    }

    @GetMapping("/reel/active/by-barcode/{barcodeId}")
    public ResponseEntity<?> getAllActiveOrderReelUsageByBarcode(@PathVariable String barcodeId) {
        try {
            List<OrderReelUsage> usages = orderReelUsageRepository.findAllByReelBarcodeIdAndCourgationOutIsNull(barcodeId);

            if (usages.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            OrderReelUsageListResponseDTO response = new OrderReelUsageListResponseDTO();
            response.setBarcodeId(barcodeId);
            response.setUsages(usages.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error fetching active order reel usages: " + e.getMessage());
        }
    }

    private OrderReelUsageResponseDTO mapToResponseDTO(OrderReelUsage usage) {
        OrderReelUsageResponseDTO response = new OrderReelUsageResponseDTO();

        if (usage.getOrder() != null) {
            response.setClient(usage.getOrder().getClient());
            response.setProductType(usage.getOrder().getProductType());
            response.setQuantity(usage.getOrder().getQuantity());
            response.setSize(usage.getOrder().getSize());
            response.setUnit(usage.getOrder().getUnit());
        }

        response.setHowManyBox(usage.getHowManyBox());
        response.setWeightConsumed(usage.getWeightConsumed());
        response.setUsageType(usage.getUsageType());
        response.setCourgationIn(usage.getCourgationIn());
        response.setCourgationOut(usage.getCourgationOut());

        if (usage.getReel() != null) {
            response.setReelSet(usage.getReel().getReelSet());
            response.setPaperType(usage.getReel().getPaperType());
            response.setGsm(usage.getReel().getGsm());
            response.setBurstFactor(usage.getReel().getBurstFactor());
            response.setDeckle(usage.getReel().getDeckle());
        }

        return response;
    }

}

