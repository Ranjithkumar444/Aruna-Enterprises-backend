package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.InventoryService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.JWTService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.ReelService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.ReelStockAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
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

    @Autowired
    private ReelUsageHistoryRepository reelUsageHistoryRepository;

    @Autowired private ReelStockThresholdRepository repository;

    @Autowired
    private ReelStockAlertRepository reelStockAlertRepository;

    @Autowired
    private ReelStockAlertService reelStockAlertService;

    @Autowired private JWTService jwtService;

    @Autowired
    private ARepository aRepository;

    @Autowired
    private BRepository bRepository;

    @Autowired
    private CRepository cRepository;

    @Autowired
    private DRepository dRepository;

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/reel/stock/alert")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createThreshold(@RequestBody ReelStockThreshold threshold) {
        Optional<ReelStockThreshold> existing = repository.findByDeckleAndGsmAndUnit(
                threshold.getDeckle(), threshold.getGsm(), threshold.getUnit());

        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Threshold already exists for this Deckle, GSM and Unit");
        }

        return ResponseEntity.ok(repository.save(threshold));
    }

    @PostMapping("/reel/stock/alert/trigger")
    public ResponseEntity<?> manualCheck() {
        reelStockAlertService.checkAndGenerateStockAlerts();
        return ResponseEntity.ok("Stock check triggered manually.");
    }



    @GetMapping("/reel/stock/alert/getAll")
    public List<ReelStockThreshold> getAllThresholds() {
        return repository.findAll();
    }

    @GetMapping("/reel/stock/alert/view")
    public List<ReelStockAlert> getUnacknowledgedAlerts() {
        return reelStockAlertRepository.findByAcknowledgedFalse();
    }


    @PostMapping("/register-reel")
    public ResponseEntity<ReelRegistrationResponseDTO> reelRegister(@RequestBody ReelDTO reeldata) {
        System.out.println("Reel No received: " + reeldata.getReelNo());
        try {
            ReelRegistrationResponseDTO response = reelService.registerReel(reeldata);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", e.getMessage() != null ? e.getMessage() : "Internal error")
                    .body(new ReelRegistrationResponseDTO(null));
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
            Long reelNo = Long.valueOf(id);
            reel = reelRepository.findByReelNo(reelNo);
        } catch (NumberFormatException e) {
            reel = reelRepository.findByBarcodeId(id);
        }

        if (reel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        ReelResponseDTO dto = new ReelResponseDTO();
        dto.setBarcodeId(reel.getBarcodeId());
        dto.setGsm(reel.getGsm());
        dto.setDeckle(reel.getDeckle());
        dto.setPreviousWeight(reel.getPreviousWeight());
        dto.setBurstFactor(reel.getBurstFactor());
        dto.setInitialWeight(reel.getInitialWeight());
        dto.setCurrentWeight(reel.getCurrentWeight());
        dto.setReelNo(reel.getReelNo());
        dto.setSupplierName(reel.getSupplierName());
        dto.setStatus(reel.getStatus());
        dto.setPaperType(reel.getPaperType());
        dto.setUnit(reel.getUnit());
        dto.setCreatedAt(reel.getCreatedAt());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/reel/orderReelUsage/{code}")
    public ResponseEntity<?> getAllOrderReelUsageByCode(@PathVariable String code) {
        try {
            List<OrderReelUsage> usages;

            usages = orderReelUsageRepository.findAllByReelBarcodeId(code);

            if (usages.isEmpty()) {
                try {
                    Long reelNo = Long.parseLong(code);
                    usages = orderReelUsageRepository.findAllByReelReelNo(reelNo);
                } catch (NumberFormatException ignored) {
                }
            }

            if (usages == null || usages.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No usage history found for code: " + code);
            }

            OrderReelUsageListResponseDTO response = new OrderReelUsageListResponseDTO();
            response.setBarcodeId(code);
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

    @GetMapping("/inventory/getInUseReelsWithDetails")
    public ResponseEntity<List<ReelWithUsageDetailsDTO>> getInUseReelsWithDetails() {
        List<Reel> reels = reelRepository.findByStatusIn(List.of(ReelStatus.IN_USE));

        List<ReelWithUsageDetailsDTO> response = reels.stream().map(reel -> {
            ReelWithUsageDetailsDTO dto = new ReelWithUsageDetailsDTO();

            dto.setBarcodeId(reel.getBarcodeId());
            dto.setReelNo(reel.getReelNo());
            dto.setInitialWeight(reel.getInitialWeight());
            dto.setCurrentWeight(reel.getCurrentWeight());
            dto.setPreviousWeight(reel.getPreviousWeight());
            dto.setPaperType(reel.getPaperType());
            dto.setGsm(reel.getGsm());
            dto.setBurstFactor(reel.getBurstFactor());
            dto.setDeckle(reel.getDeckle());
            dto.setUsageType(reel.getReelSet());

            List<OrderReelUsage> usages = orderReelUsageRepository.findByReelId(reel.getId());
            List<OrderUsageDetailsDTO> usageDTOs = usages.stream().map(usage -> {
                OrderUsageDetailsDTO usageDTO = new OrderUsageDetailsDTO();
                Order order = usage.getOrder();

                usageDTO.setClient(order.getClient());
                usageDTO.setProductType(order.getProductType());
                usageDTO.setQuantity(order.getQuantity());
                usageDTO.setSize(order.getSize());
                usageDTO.setUnit(order.getUnit());
                usageDTO.setWeightConsumed(usage.getWeightConsumed());
                usageDTO.setCourgationIn(usage.getCourgationIn());
                usageDTO.setCourgationOut(usage.getCourgationOut());
                usageDTO.setHowManyBox(usage.getHowManyBox());

                return usageDTO;
            }).collect(Collectors.toList());

            dto.setOrderUsages(usageDTOs);
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/inventory/manipulateReelData")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> manipulateReel(@RequestBody ReelManipulationRequest request) {
        try {
            String searchTerm = request.getReelNoOrBarcodeId().trim();

            Reel existingReel = findReelByAnyIdentifier(searchTerm);

            if (existingReel == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Reel not found for identifier: " + searchTerm));
            }

            updateReelFields(existingReel, request);

            Reel savedReel = reelRepository.save(existingReel);

            return ResponseEntity.ok(createSuccessResponse(savedReel));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error manipulating reel: " + (e.getMessage() != null ? e.getMessage() : "Unknown error")));
        }
    }

    private Reel findReelByAnyIdentifier(String searchTerm) {
        Reel reel = reelRepository.findByBarcodeId(searchTerm);
        if (reel != null) return reel;

        reel = reelRepository.findByBarcodeIdIgnoreCase(searchTerm);
        if (reel != null) return reel;

        try {
            Long reelNo = Long.parseLong(searchTerm);
            reel = reelRepository.findByReelNo(reelNo);
            if (reel != null) return reel;

            reel = reelRepository.findByBarcodeIdOrReelNo(searchTerm, reelNo);
            if (reel != null) return reel;
        } catch (NumberFormatException ignored) {}

        List<Reel> reels = reelRepository.findByBarcodeIdContainingIgnoreCase(searchTerm);
        if (!reels.isEmpty()) {
            return reels.get(0);
        }
        return null;
    }

    private void updateReelFields(Reel reel, ReelManipulationRequest request) {
        if (request.getInitialWeight() != 0) {
            reel.setInitialWeight(request.getInitialWeight());
        }
        if (request.getCurrentWeight() != 0) {
            reel.setCurrentWeight(request.getCurrentWeight());
        }
        if (request.getPreviousWeight() != null) {
            reel.setPreviousWeight(request.getPreviousWeight());
        }
        if (request.getUnit() != null && !request.getUnit().trim().isEmpty()) {
            reel.setUnit(request.getUnit());
        }
        if (request.getStatus() != null) {
            reel.setStatus(request.getStatus());
        }
    }

    private Map<String, Object> createSuccessResponse(Reel reel) {
        return Map.of(
                "status", "success",
                "message", "Reel updated successfully",
                "data", Map.of(
                        "id", reel.getId(),
                        "barcodeId", reel.getBarcodeId(),
                        "reelNo", reel.getReelNo(),
                        "currentWeight", reel.getCurrentWeight(),
                        "status", reel.getStatus()
                )
        );
    }

    private Map<String, Object> createErrorResponse(String message) {
        return Map.of(
                "status", "error",
                "message", message,
                "timestamp", LocalDateTime.now()
        );
    }

    @GetMapping("/reel/usage/{barcodeId}")
    public ResponseEntity<List<ReelUsageHistory>> reelUsage(@PathVariable("barcodeId") String id) {
        List<ReelUsageHistory> usageHistoryList = new ArrayList<>();

        try {
            Long reelNo = Long.valueOf(id); // if input is a number, treat as reelNo
            usageHistoryList = reelUsageHistoryRepository.findByReelNo(reelNo);
        } catch (NumberFormatException e) {
            // if not a number, treat it as barcodeId
            usageHistoryList = reelUsageHistoryRepository.findByBarcodeId(id);
        }

        if (usageHistoryList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(usageHistoryList);
        }

        return ResponseEntity.ok(usageHistoryList);
    }

}

