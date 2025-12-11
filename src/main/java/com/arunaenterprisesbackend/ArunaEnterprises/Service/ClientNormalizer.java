package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Order;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClientNormalizer {

    private final OrderRepository orderRepository;

    // @PostConstruct
    public void normalizeOldClients() {
        List<Order> all = orderRepository.findAll();
        for (Order o : all) {
            if (o.getClient() != null && (o.getNormalizedClient() == null || o.getNormalizedClient().isBlank())) {
                o.setNormalizedClient(o.getClient().toLowerCase().replaceAll("[^a-z0-9]", ""));
            }
        }
        orderRepository.saveAll(all);
        System.out.println("✅ Normalized old client names successfully!");
    }
}
