package com.arunaenterprisesbackend.ArunaEnterprises.Controller;


import com.arunaenterprisesbackend.ArunaEnterprises.DTO.Barcode;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.BarcodeDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.CalculationDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ContactDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderReelUsage;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.AttendanceService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.OrderService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.WeightCalculation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@RestController
@CrossOrigin("*")
@RequestMapping("/public")
public class PublicController {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

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

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderReelUsageRepository orderReelUsageRepository;

    @GetMapping("/greet")
    public String HelloController(){
        return "Hello World";
    }

    @PostMapping("/scan-attendance")
    public ResponseEntity<String> scanAttendance(@RequestBody Barcode barcodeId) {
        try {
            String response = String.valueOf(attendanceService.markAttendance(barcodeId.getBarcodeId()));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }

    @PostMapping("/attendance-scan")
    public String attendanceScan(@RequestBody  Barcode barcodeId){
        System.out.println(barcodeId.getBarcodeId());
        return barcodeId.getBarcodeId();
    }

    @PostMapping("/contact-details")
    public ResponseEntity<String> handleContactForm(@RequestBody ContactDTO request) {
        try {
            ContactMessage contactMessage = new ContactMessage();

            contactMessage.setName(request.getName());
            contactMessage.setPhone(request.getPhone());
            contactMessage.setMessage(request.getMessage());
            contactMessage.setCreatedAt(ZonedDateTime.now(IST_ZONE));
            contactMessage.setMessage(request.getMessage());
            contactMessage.setReplyStatus(false);

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
            System.out.println("Received barcodeId for starting use: '" + barcodeId + "' for Order ID: " + barcodeDTO.getOrderId());

            Reel reel = reelRepository.findByBarcodeId(barcodeId);
            if (reel == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reel not found for barcode ID: " + barcodeId);
            }
            if (reel.getStatus() == ReelStatus.USE_COMPLETED) {
                return ResponseEntity.badRequest().body("Reel with barcode " + barcodeId + " has already completed its use and cannot be reused.");
            }

            Order order = orderRepository.findById(barcodeDTO.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + barcodeDTO.getOrderId()));

            Optional<OrderReelUsage> existingActiveUsage = orderReelUsageRepository.findByReelBarcodeIdAndCourgationOutIsNull(barcodeId);

            if (existingActiveUsage.isPresent()) {
                OrderReelUsage currentUsage = existingActiveUsage.get();

                if (currentUsage.getOrder().getId().equals(order.getId())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("Reel " + barcodeId + " is already IN_USE for this order (" + order.getId() + "). No new session started.");
                } else {
                    currentUsage.setCourgationOut(ZonedDateTime.now(IST_ZONE));
                    orderReelUsageRepository.save(currentUsage);
                    System.out.println("Previous usage session for Reel " + barcodeId + " (Order " + currentUsage.getOrder().getId() + ") ended automatically.");

                    reel.setStatus(ReelStatus.PARTIALLY_USED_AVAILABLE);
                    reelRepository.save(reel);
                }
            }

            OrderReelUsage newOrderReelUsage = new OrderReelUsage();
            newOrderReelUsage.setReel(reel);
            newOrderReelUsage.setOrder(order);
            newOrderReelUsage.setCourgationIn(ZonedDateTime.now(IST_ZONE));
            newOrderReelUsage.setUsageType(barcodeDTO.getReelSet());
            newOrderReelUsage.setWeightConsumed(0.0);

            orderReelUsageRepository.save(newOrderReelUsage);

            reel.setReelSet(barcodeDTO.getReelSet());
            reel.setStatus(ReelStatus.IN_USE);
            reelRepository.save(reel);

            if (order.getStatus() == OrderStatus.TODO) {
                order.setStatus(OrderStatus.IN_PROGRESS);
                orderRepository.save(order);
            }

            return ResponseEntity.ok("Reel " + barcodeId + " status set to InUse and new usage session started for order " + order.getId() + ".");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/inventory/reelWeightCalculation")
    public ResponseEntity<String> calculateWeight(@RequestBody CalculationDTO calculationDTO) {
        try {
            String barcodeId = calculationDTO.getBarcodeId().trim();

            Reel reel = reelRepository.findByBarcodeId(barcodeId);
            if (reel == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reel not found for barcode ID: " + barcodeId);
            }

            if (reel.getStatus() != ReelStatus.IN_USE) {
                return ResponseEntity.badRequest().body("The reel (ID: " + barcodeId + ") is not currently IN_USE for any order. Please start a usage session first.");
            }

            Optional<OrderReelUsage> optionalOrderReelUsage = orderReelUsageRepository.findByReelBarcodeIdAndCourgationOutIsNull(barcodeId);

            OrderReelUsage orderReelUsage = optionalOrderReelUsage.orElseThrow(
                    () -> new IllegalStateException("No active OrderReelUsage found for reel with barcode ID: " + barcodeId + ". Reel status is IN_USE but no matching usage record exists with courgationOut = null.")
            );

            Order associatedOrder = orderReelUsage.getOrder();

            String reelSet = reel.getReelSet();
            double usedWeightGrams = weightCalculation.calculateWeight(calculationDTO, reelSet);
            double usedWeightKg = usedWeightGrams / 1000.0;

            reel.setPreviousWeight(reel.getCurrentWeight());
            int currentWeight = (int) (reel.getCurrentWeight() - usedWeightKg);
            reel.setCurrentWeight(currentWeight);

            orderReelUsage.setCourgationOut(ZonedDateTime.now(IST_ZONE));
            if (currentWeight <= 10) {
                reel.setStatus(ReelStatus.USE_COMPLETED);
                System.out.println("Reel " + barcodeId + " fully consumed. Setting reel status to USE_COMPLETED.");
            } else {
                reel.setStatus(ReelStatus.PARTIALLY_USED_AVAILABLE);
                System.out.println("Reel " + barcodeId + " has remaining weight. Setting reel status to PARTIALLY_USED_AVAILABLE.");
            }

            if (calculationDTO.getOrderCompleted() != null && calculationDTO.getOrderCompleted().equalsIgnoreCase("yes")) {
                associatedOrder.setStatus(OrderStatus.COMPLETED);
                System.out.println("Order " + associatedOrder.getId() + " marked as COMPLETED.");
            } else if (associatedOrder.getStatus() == OrderStatus.TODO) {
                associatedOrder.setStatus(OrderStatus.IN_PROGRESS);
            }

            reelRepository.save(reel);
            orderRepository.save(associatedOrder);

            orderReelUsage.setWeightConsumed(orderReelUsage.getWeightConsumed() + usedWeightKg);
            orderReelUsage.setRecordedBy(calculationDTO.getScannedBy());
            orderReelUsage.setHowManyBox(calculationDTO.getNoOfBoxMade());
            orderReelUsage.setPreviousWeight(reel.getPreviousWeight());
            orderReelUsageRepository.save(orderReelUsage);

            ReelUsageHistory reelUsageHistory = new ReelUsageHistory();
            reelUsageHistory.setBarcodeId(barcodeId);
            reelUsageHistory.setReelSet(reel.getReelSet());
            reelUsageHistory.setUsedWeight(usedWeightKg);
            reelUsageHistory.setUsedAt(ZonedDateTime.now(IST_ZONE));
            reelUsageHistory.setBoxDetails(String.format("%d x %d x %d",
                    calculationDTO.getLength(), calculationDTO.getWidth(), calculationDTO.getHeight()));
            reelUsageHistory.setUsedBy(calculationDTO.getScannedBy());
            reelUsageHistoryRepository.save(reelUsageHistory);

            return ResponseEntity.ok(String.format(
                    "Used Weight = %.2f kg, Current Reel Weight = %d kg. Order %s status: %s. Reel session closed.",
                    usedWeightKg, currentWeight, associatedOrder.getId(), associatedOrder.getStatus()));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An internal error occurred: " + e.getMessage());
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

    @GetMapping("/order/getOrdersByActiveStatus")
    public ResponseEntity<List<Order>> getOrdersByActiveStatus() {
        List<OrderStatus> activeStatuses = Arrays.asList(
                OrderStatus.TODO,
                OrderStatus.IN_PROGRESS
        );
        List<Order> orders = orderRepository.findByStatusIn(activeStatuses);
        return ResponseEntity.ok(orders);
    }

}