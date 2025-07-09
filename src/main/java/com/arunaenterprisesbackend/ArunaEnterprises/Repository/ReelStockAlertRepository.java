package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStockAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReelStockAlertRepository extends JpaRepository<ReelStockAlert, Long> {

    List<ReelStockAlert> findByAcknowledgedFalse();

    List<ReelStockAlert> findByUnitAndAcknowledgedFalse(String unit);


}