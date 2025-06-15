package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.OrderDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Order;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderReelUsage;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderReelUsageRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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

    @PostMapping("/order/create-order")
    public ResponseEntity<String> createOrderCnt(@RequestBody OrderDTO order) {
        try {
            String result = orderservice.createOrder(order);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create order: " + e.getMessage());
        }
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
        List<Order> orders = orderRepository.findByStatusIn(activeStatuses);
        return ResponseEntity.ok(orders);
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
}
