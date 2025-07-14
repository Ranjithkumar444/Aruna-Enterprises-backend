package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Order;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderSuggestedReelsRepository;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusListener {

    @Autowired
    private OrderSuggestedReelsRepository repo;

    @PostUpdate
    public void afterUpdate(Order order) {
        if (order.getStatus() == OrderStatus.COMPLETED) {
            repo.deleteByOrder(order);
        }
    }
}
