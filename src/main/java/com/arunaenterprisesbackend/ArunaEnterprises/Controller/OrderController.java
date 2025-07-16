package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.OrderDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.OrderSplitDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.SuggestedReelsResponseDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Order;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderReelUsage;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ProductionDetail;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderReelUsageRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderSuggestedReelsRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.OrderService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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


    @PostMapping("/order/split")
    public ResponseEntity<String> splitOrder(@RequestBody OrderSplitDTO dto) {
        try {
            orderservice.splitOrder(dto);
            return ResponseEntity.ok("Order split successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to split order: " + e.getMessage());
        }
    }

    @GetMapping("/{orderId}/production-detail")
    public ResponseEntity<ProductionDetail> getProductionDetailByOrderId(@PathVariable Long orderId) {
        ProductionDetail pd = orderservice.getProductionDetailByOrderId(orderId);
        return ResponseEntity.ok(pd);
    }
    

    @PutMapping("/order/{id}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        try {
            OrderStatus status = OrderStatus.valueOf(payload.get("status").toString());
            String transportNumber = payload.containsKey("transportNumber") ?
                    payload.get("transportNumber").toString() : null;

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
                OrderStatus.COMPLETED
        );

        // Fetch TODO, IN_PROGRESS, COMPLETED orders
        List<Order> activeOrders = orderRepository.findByStatusIn(activeStatuses);

        // Get current time in Central US time zone
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Chicago"));
        ZonedDateTime cutoff = now.minusDays(1);

        // Fetch SHIPPED orders shipped within last 24 hours
        List<Order> recentShippedOrders = orderRepository.findByStatusAndShippedAtAfter(
                OrderStatus.SHIPPED,
                cutoff
        );

        activeOrders.addAll(recentShippedOrders);

        return ResponseEntity.ok(activeOrders);
    }

    @GetMapping("/order/getOrdersToDoAndInProgress")
    public ResponseEntity<List<Order>> GetOrdersByActiveStatus() {
        List<OrderStatus> activeStatuses = Arrays.asList(
                OrderStatus.TODO,
                OrderStatus.IN_PROGRESS
        );

        List<Order> activeOrders = orderRepository.findByStatusIn(activeStatuses);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Chicago"));
        ZonedDateTime cutoff = now.minusDays(1);

        return ResponseEntity.ok(activeOrders);
    }


    @GetMapping("/order/getCompletedOrders")
    public ResponseEntity<List<Order>> getOrderCompletedOrders() {
        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.COMPLETED
        );
        List<Order> orders = orderRepository.findByStatusIn(activeStatuses);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/order/history/getAllOrderReelUsage")
    public ResponseEntity<List<OrderReelUsage>> getAllOrderReelUsage(){
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
}
