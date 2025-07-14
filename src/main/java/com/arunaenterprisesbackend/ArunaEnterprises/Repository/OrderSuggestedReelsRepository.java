package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Order;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderSuggestedReels;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderSuggestedReelsRepository extends JpaRepository<OrderSuggestedReels, Long> {

    Optional<OrderSuggestedReels> findByOrder(Order order);

    // Corrected Query: Use the actual field names from your entity
    @Modifying
    @Transactional
    @Query("DELETE FROM OrderSuggestedReels osr WHERE osr.order = :order")
    void deleteByOrder(@Param("order") Order order);

    @Query("SELECT o FROM OrderSuggestedReels o WHERE o.order.client = ?1")
    List<OrderSuggestedReels> findByClient(String client);

    Optional<OrderSuggestedReels> findFullByOrderId(@Param("orderId") Long orderId);
}