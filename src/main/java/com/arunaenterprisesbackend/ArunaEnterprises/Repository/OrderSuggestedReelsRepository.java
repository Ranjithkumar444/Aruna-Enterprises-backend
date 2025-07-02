package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Order;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderSuggestedReels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderSuggestedReelsRepository extends JpaRepository<OrderSuggestedReels, Long> {

    Optional<OrderSuggestedReels> findByOrder(Order order);

    @Modifying
    @Query("DELETE FROM OrderSuggestedReels osr WHERE osr.order = :order")
    void deleteByOrder(@Param("order") Order order);

    @Query("SELECT osr FROM OrderSuggestedReels osr JOIN FETCH osr.topReels JOIN FETCH osr.bottomReels LEFT JOIN FETCH osr.fluteReels WHERE osr.order.id = :orderId")
    Optional<OrderSuggestedReels> findFullByOrderId(@Param("orderId") Long orderId);
}