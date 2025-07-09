package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReelUsageHistoryRepository extends JpaRepository<ReelUsageHistory, Long> {
    List<ReelUsageHistory> findByBarcodeId(String barcodeId);
    List<ReelUsageHistory> findByReelNo(Long reelNo);
}

