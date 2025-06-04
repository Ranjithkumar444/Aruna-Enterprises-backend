package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.OrderDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Order;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class OrderController {

    @Autowired
    private OrderService orderservice;

    @Autowired
    private OrderRepository orderRepository;

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
}
