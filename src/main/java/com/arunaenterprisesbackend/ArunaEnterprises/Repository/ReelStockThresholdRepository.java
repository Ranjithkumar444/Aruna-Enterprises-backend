package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStockThreshold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReelStockThresholdRepository extends JpaRepository<ReelStockThreshold, Long> {
    Optional<ReelStockThreshold> findByDeckleAndGsmAndUnit(int deckle, int gsm, String unit);
}
