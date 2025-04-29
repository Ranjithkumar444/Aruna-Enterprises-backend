package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReelRepository extends JpaRepository<Reel, String> {
    Reel findByBarcodeId(String barcodeId);
}
