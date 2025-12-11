package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Order;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderReelUsage;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderReelUsageRepository extends JpaRepository<OrderReelUsage,Long> {
    Optional<OrderReelUsage> findByReelBarcodeId(String barcodeId);

    Optional<OrderReelUsage> findByReelBarcodeIdAndCourgationOutIsNull(String barcodeId);

    List<OrderReelUsage> findByReelId(Long reelId);

    List<OrderReelUsage> findAllByReelBarcodeId(String barcodeId);
    List<OrderReelUsage> findAllByReelBarcodeIdAndCourgationOutIsNull(String barcodeId);

    List<OrderReelUsage> findAllByReelReelNo(Long reelNo);

    List<OrderReelUsage> findByOrderIn(List<Order> orders);

    @Query("SELECT u FROM OrderReelUsage u " +
            "WHERE u.courgationOut BETWEEN :start AND :end")
    List<OrderReelUsage> findUsagesInPeriod(
            @Param("start") ZonedDateTime start,
            @Param("end") ZonedDateTime end
    );

    @Query("SELECT o FROM OrderReelUsage o WHERE o.usageType = ?1 AND o.order.status = 'SHIPPED'")
    List<OrderReelUsage> findByUsageTypeAndShippedOrder(String usageType);


    List<OrderReelUsage> findByOrder(Order order);

    List<OrderReelUsage> findByReel(Reel reel);

    //    THIS IS FOR DAILY REPORTING PURPOSE
//    ***********************************
    @Query("SELECT u FROM OrderReelUsage u WHERE " +
            "(u.courgationIn >= :start AND u.courgationIn < :end) " +
            "OR (u.courgationOut >= :start AND u.courgationOut < :end)")
    List<OrderReelUsage> findByCourgationDateRange(
            @Param("start") ZonedDateTime start,
            @Param("end") ZonedDateTime end
    );
}
