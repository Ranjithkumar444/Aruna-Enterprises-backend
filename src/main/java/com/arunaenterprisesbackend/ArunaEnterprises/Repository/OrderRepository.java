package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Order;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    // For multiple statuses
    List<Order> findByStatusIn(List<OrderStatus> statuses);

    // For single status - rename to findByStatus
    List<Order> findByStatus(OrderStatus status);

    // Other methods remain the same...
    List<Order> findByStatusAndShippedAtAfter(OrderStatus status, ZonedDateTime cutoff);
    List<Order> findByStatusAndShippedAtBefore(OrderStatus orderStatus, LocalDateTime cutoff);
    List<Order> findByNormalizedClient(String normalizedClient);

    List<Order> findByStatusAndShippedAtBetween(
            OrderStatus status,
            ZonedDateTime start,
            ZonedDateTime end
    );

    @Query("SELECT o FROM Order o WHERE o.normalizedClient = ?1 AND o.size = ?2")
    List<Order> findByNormalizedClientAndSize(String normalizedClient, String size);

    @Query("SELECT o FROM Order o WHERE o.status IN ('COMPLETED', 'SHIPPED') ORDER BY o.shippedAt DESC")
    List<Order> findCompletedAndShippedOrders();
}