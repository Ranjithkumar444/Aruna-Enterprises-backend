package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.OrderDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Order;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderStatus;
import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public String createOrder(OrderDTO order) {

        try{
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
            order.setUpdatedAt(LocalDateTime.now());
            order1.setUnit(order.getUnit());
            orderRepository.save(order1);

            return "Order Created";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in creating Order - " + e;
        }

    }

    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.COMPLETED && newStatus == OrderStatus.COMPLETED) {
            if (order.getCompletedAt() == null) {
                order.setCompletedAt(LocalDateTime.now());
            }
        } else if (newStatus == OrderStatus.COMPLETED) {
            order.setStatus(OrderStatus.COMPLETED);
            order.setCompletedAt(LocalDateTime.now());
        } else {
            order.setStatus(newStatus);
            order.setCompletedAt(null);
        }

        orderRepository.save(order);
    }


}
