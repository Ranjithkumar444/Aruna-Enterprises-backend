package com.arunaenterprisesbackend.ArunaEnterprises.Repository;


import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReelRepository extends JpaRepository<Reel, Long> {

    Reel findByBarcodeId(String barcodeId);

    Reel findByBarcodeIdOrReelNo(String barcodeId, Long reelNo);

    Reel findByReelNo(Long reelNo);

    List<Reel> findBySupplierName(String supplierName);

    List<Reel> findByStatusIn(List<ReelStatus> statuses);

}