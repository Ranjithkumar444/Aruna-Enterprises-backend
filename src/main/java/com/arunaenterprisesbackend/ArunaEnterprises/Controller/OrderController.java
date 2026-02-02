package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderReelUsageRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderSuggestedReelsRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.OrderService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class OrderController {

    @Autowired
    private OrderService orderservice;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderReelUsageRepository orderReelUsageRepository;

    @Autowired
    private OrderSuggestedReelsRepository orderSuggestedReelsRepository;

    @Autowired
    private com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelRepository reelRepository;

    @PostMapping("/order/create-order")
    public ResponseEntity<SuggestedReelsResponseDTO> createOrderCnt(@RequestBody OrderDTO order) {
        try {
            SuggestedReelsResponseDTO response = orderservice.createOrder(order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SuggestedReelsResponseDTO error = new SuggestedReelsResponseDTO();
            error.setMessage("Failed to create order: " + e.getMessage());
            error.setTopGsmReels(Collections.emptyList());
            error.setBottomGsmReels(Collections.emptyList());
            error.setFluteGsmReels(Collections.emptyList());
            error.setFluteRequired(false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{orderId}/production-detail")
    public ResponseEntity<ProductionDetail> getProductionDetailByOrderId(@PathVariable Long orderId) {
        ProductionDetail pd = orderservice.getProductionDetailByOrderId(orderId);
        return ResponseEntity.ok(pd);
    }

    @PostMapping("/order/{orderId}/split")
    public ResponseEntity<String> splitOrder(
            @PathVariable Long orderId,
            @RequestBody OrderSplitDTO dto) {
        try {
            // Pass the path variable ID to the service method
            orderservice.splitOrder(orderId, dto.getFirstOrderQuantity(), dto.getSecondOrderQuantity());
            return ResponseEntity.ok("Order split successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to split order: " + e.getMessage());
        }
    }

    @GetMapping("/order/getInStockCompletedOrders")
    public ResponseEntity<List<Order>> getAllInStockCompletedOrder() {
        try {
            List<Order> orders = orderRepository.findByStatus(OrderStatus.COMPLETED);

            return ResponseEntity.ok().body(orders);
        } catch (Exception e) {
            return (ResponseEntity<List<Order>>) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/order/{id}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        try {
            OrderStatus status = OrderStatus.valueOf(payload.get("status").toString());
            String transportNumber = payload.containsKey("transportNumber") ? payload.get("transportNumber").toString()
                    : null;

            orderservice.updateOrderStatus(id, status, transportNumber);
            return ResponseEntity.ok("Order status updated to " + status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update status: " + e.getMessage());
        }
    }

    @GetMapping("/order/getAllOrders")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/order/getOrdersByActiveStatus")
    public ResponseEntity<List<Order>> getOrdersByActiveStatus() {
        List<OrderStatus> activeStatuses = Arrays.asList(
                OrderStatus.TODO,
                OrderStatus.IN_PROGRESS,
                OrderStatus.COMPLETED);

        // Fetch TODO, IN_PROGRESS, COMPLETED orders
        List<Order> activeOrders = orderRepository.findByStatusIn(activeStatuses);

        // Get current time in Central US time zone
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Chicago"));
        ZonedDateTime cutoff = now.minusDays(1);

        // Fetch SHIPPED orders shipped within last 24 hours
        List<Order> recentShippedOrders = orderRepository.findByStatusAndShippedAtAfter(
                OrderStatus.SHIPPED,
                cutoff);

        activeOrders.addAll(recentShippedOrders);

        return ResponseEntity.ok(activeOrders);
    }

    @GetMapping("/order/getOrdersToDoAndInProgress")
    public ResponseEntity<List<Order>> GetOrdersByActiveStatus() {
        List<OrderStatus> activeStatuses = Arrays.asList(
                OrderStatus.TODO,
                OrderStatus.IN_PROGRESS);

        List<Order> activeOrders = orderRepository.findByStatusIn(activeStatuses);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Chicago"));
        ZonedDateTime cutoff = now.minusDays(1);

        return ResponseEntity.ok(activeOrders);
    }

    @GetMapping("/order/getCompletedOrders")
    public ResponseEntity<List<Order>> getOrderCompletedOrders() {
        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.COMPLETED);
        List<Order> orders = orderRepository.findByStatusIn(activeStatuses);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/order/history/getAllOrderReelUsage")
    public ResponseEntity<List<OrderReelUsage>> getAllOrderReelUsage() {
        List<OrderReelUsage> listoforder = orderReelUsageRepository.findAll();

        return ResponseEntity.ok(listoforder);
    }

    @GetMapping("/order/{orderId}/suggested-reels")
    public ResponseEntity<SuggestedReelsResponseDTO> getSuggestedReels(@PathVariable Long orderId) {
        try {
            SuggestedReelsResponseDTO response = orderservice.getSuggestedReels(orderId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";

            SuggestedReelsResponseDTO errorResponse = new SuggestedReelsResponseDTO();
            errorResponse.setTopGsmReels(Collections.emptyList());
            errorResponse.setBottomGsmReels(Collections.emptyList());
            errorResponse.setFluteGsmReels(Collections.emptyList());
            errorResponse.setFluteRequired(false);
            errorResponse.setMessage("Error retrieving suggested reels: " + errorMessage);

            HttpStatus status;
            if (errorMessage.contains("Order not found") || errorMessage.contains("No suggestions found")) {
                status = HttpStatus.NOT_FOUND;
            } else if (errorMessage.contains("Suggestions not available for completed orders")) {
                status = HttpStatus.BAD_REQUEST;
            } else {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }

            return ResponseEntity.status(status).body(errorResponse);
        }
    }

    private ResponseEntity<?> handleException(RuntimeException e) {
        if (e.getMessage().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } else if (e.getMessage().contains("not available")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred"));
    }

    @Data
    @AllArgsConstructor
    private static class ErrorResponse {
        private String message;
    }

    // THIS IS FOR DAILY REPORTING PURPOSE
    // ***********************************
    @GetMapping("/order/daily-production-usage")
    public ResponseEntity<Map<String, List<DailyProductionUsageDTO>>> getDailyProductionUsage(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            // 1. Define Timezone (IST)
            ZoneId istZone = ZoneId.of("Asia/Kolkata");

            // 2. Calculate Start and End of Today or Selected Date
            LocalDate targetDate = (date != null) ? date : LocalDate.now(istZone);
            ZonedDateTime startOfDay = targetDate.atStartOfDay(istZone);
            ZonedDateTime endOfDay = startOfDay.plusDays(1);

            // 3. Fetch records
            List<OrderReelUsage> usages = orderReelUsageRepository.findByCourgationDateRange(startOfDay, endOfDay);

            // 4. Stream, Map, and Group by Unit
            Map<String, List<DailyProductionUsageDTO>> response = usages.stream()
                    .collect(Collectors.groupingBy(
                            // Key Mapper: Group by Order Unit (Handle nulls safely)
                            usage -> (usage.getOrder() != null && usage.getOrder().getUnit() != null)
                                    ? usage.getOrder().getUnit()
                                    : "Unknown Unit",

                            // Map Factory: Sort Units Alphabetically (Unit A, Unit B...)
                            TreeMap::new,

                            // Downstream Collector: Map Entity to DTO
                            Collectors.mapping(usage -> {
                                Order order = usage.getOrder();
                                Reel reel = usage.getReel();

                                return new DailyProductionUsageDTO(
                                        // OrderReelUsage Fields
                                        usage.getId(),
                                        usage.getWeightConsumed(),
                                        usage.getCourgationIn() != null
                                                ? usage.getCourgationIn().withZoneSameInstant(istZone)
                                                : null,
                                        usage.getCourgationOut() != null
                                                ? usage.getCourgationOut().withZoneSameInstant(istZone)
                                                : null,
                                        usage.getRecordedBy(),
                                        usage.getUsageType(),
                                        usage.getHowManyBox(),
                                        usage.getPreviousWeight(),

                                        // Order Fields
                                        order != null ? order.getClient() : "N/A",
                                        order != null ? order.getProductType() : "N/A",
                                        order != null ? order.getTypeOfProduct() : "N/A",
                                        order != null ? order.getProductName() : "N/A",
                                        order != null ? order.getQuantity() : 0,
                                        order != null ? order.getSize() : "N/A",

                                        // Reel Fields
                                        reel != null ? reel.getBarcodeId() : "N/A",
                                        reel != null ? reel.getReelNo() : 0L,
                                        reel != null ? reel.getGsm() : 0,
                                        reel != null ? reel.getBurstFactor() : 0,
                                        reel != null ? reel.getDeckle() : 0,
                                        reel != null ? reel.getPaperType() : "N/A");
                            }, Collectors.toList())));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/reels/stock-summary")
    public ResponseEntity<List<com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelStockSummaryDTO>> getReelStockSummary() {
        try {
            List<com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelStockSummaryDTO> summary = reelRepository
                    .findReelStockSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}