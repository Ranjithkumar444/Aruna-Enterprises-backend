package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Order;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderReelUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderReelUsageRepository extends JpaRepository<OrderReelUsage,Long> {
    Optional<OrderReelUsage> findByReelBarcodeId(String barcodeId);

    List<OrderReelUsage> findByReelId(Long reelId);

    Optional<OrderReelUsage> findByReelBarcodeIdAndCourgationOutIsNull(String barcodeId);

    List<OrderReelUsage> findAllByReelBarcodeId(String barcodeId);
    List<OrderReelUsage> findAllByReelBarcodeIdAndCourgationOutIsNull(String barcodeId);
    List<OrderReelUsage> findAllByReelReelNo(Long reelNo);

    List<OrderReelUsage> findByOrderIn(List<Order> orders);


}
