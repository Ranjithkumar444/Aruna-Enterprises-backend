package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.OrderDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Order;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public String createOrder(OrderDTO order) {
        try {
            Order order1 = new Order();
            order1.setClient(order.getClient());
            order1.setCreatedAt(LocalDateTime.now());
            order1.setCreatedBy(order.getCreatedBy());
            order1.setSize(order.getSize());
            order1.setStatus(OrderStatus.TODO);
            order1.setDeliveryAddress(order.getDeliveryAddress());
            order1.setQuantity(order.getQuantity());
            order1.setExpectedCompletionDate(order.getExpectedCompletionDate());
            order1.setProductType(order.getProductType());
            order1.setMaterialGrade(order.getMaterialGrade());
            order1.setUpdatedAt(LocalDateTime.now());
            order1.setUnit(order.getUnit());
            order1.setTransportNumber(order.getTransportNumber());
            orderRepository.save(order1);

            return "Order Created";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in creating Order - " + e;
        }
    }

    public void updateOrderStatus(Long orderId, OrderStatus newStatus, String transportNumber) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        if (newStatus == OrderStatus.COMPLETED) {
            order.setCompletedAt(LocalDateTime.now());
            order.setShippedAt(null);
            order.setTransportNumber(null);
        } else if (newStatus == OrderStatus.SHIPPED) {
            order.setShippedAt(LocalDateTime.now());
            order.setTransportNumber(transportNumber);
        } else {
            order.setCompletedAt(null);
            order.setShippedAt(null);
            order.setTransportNumber(null);
        }

        orderRepository.save(order);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupOldShippedOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<Order> oldShippedOrders = orderRepository.findByStatusAndShippedAtBefore(
                OrderStatus.SHIPPED, cutoff);

        orderRepository.deleteAll(oldShippedOrders);
    }
}
