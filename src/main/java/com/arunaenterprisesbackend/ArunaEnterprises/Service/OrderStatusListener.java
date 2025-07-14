package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Order;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderSuggestedReelsRepository;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusListener {

    private static OrderSuggestedReelsRepository staticRepo;

    @Autowired
    public void setRepo(OrderSuggestedReelsRepository repo) {
        OrderStatusListener.staticRepo = repo;
    }

    @PostUpdate
    public void afterUpdate(Order order) {
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.SHIPPED) {
            staticRepo.deleteByOrder(order);
        }
    }
}
