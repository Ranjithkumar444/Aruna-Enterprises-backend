package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface ReelUsageHistoryRepository extends JpaRepository<ReelUsageHistory, Long> {
    List<ReelUsageHistory> findByBarcodeId(String barcodeId);
    List<ReelUsageHistory> findByReelNo(Long reelNo);
    List<ReelUsageHistory> findByUsedAtBetween(ZonedDateTime start, ZonedDateTime end);

    @Query("SELECT r FROM ReelUsageHistory r WHERE r.usageType = :usageType AND r.usedAt BETWEEN :start AND :end")
    List<ReelUsageHistory> findByUsageTypeAndUsedAtBetween(
            @Param("usageType") String usageType,
            @Param("start") ZonedDateTime start,
            @Param("end") ZonedDateTime end
    );


    @Query("SELECT r FROM ReelUsageHistory r WHERE r.reelSet = ?1 ORDER BY r.usedAt DESC")
    List<ReelUsageHistory> findByReelSet(String reelSet);

    @Query("SELECT r FROM ReelUsageHistory r WHERE r.usageType = ?1 AND r.usedAt BETWEEN ?2 AND ?3")
    List<ReelUsageHistory> findByUsageTypeAndDateBetween(String usageType, ZonedDateTime start, ZonedDateTime end);
}

