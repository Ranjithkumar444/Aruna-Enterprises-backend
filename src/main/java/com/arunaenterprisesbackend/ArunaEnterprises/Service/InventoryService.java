package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Utility.BarcodeGenerator;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class InventoryService {

    @Autowired
    public ReelRepository reelRepository;

    public String registerReel(ReelDTO reeldata) throws Exception {

        Reel reel = new Reel();

        reel.setSize(reeldata.getSize());
        reel.setStatus(ReelStatus.valueOf(reeldata.getStatus()));
        reel.setQuality(reeldata.getQuality());
        reel.setWeight(reeldata.getWeight());
        reel.setArrivalDate(LocalDate.now());
        reel.setCreatedBy(reeldata.getCreatedBy());
        reel.setSupplierName(reeldata.getSupplierName());

        String barcodeId = String.valueOf(UUID.randomUUID());
        reel.setBarcodeId(barcodeId);

        byte[] barcodeImage = BarcodeGenerator.generateBarcodeImage(barcodeId);
        reel.setBarcodeImage(barcodeId);

        reelRepository.save(reel);

        return "Reel Register with ID" + barcodeId;
    }
}
