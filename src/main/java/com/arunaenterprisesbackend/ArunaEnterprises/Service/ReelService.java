package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Utility.BarcodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReelService {

    @Autowired
    private ReelRepository reelRepository;

    public String registerReel(ReelDTO reeldata) {
        try {
            Reel reel = new Reel();
            reel.setStatus(ReelStatus.NOT_IN_USE);
            reel.setDeckle(reeldata.getDeckle());
            reel.setGsm(reeldata.getGsm());
            reel.setHeight(reeldata.getHeight());
            reel.setLength(reeldata.getLength());
            reel.setBurstFactor(reeldata.getBurstFactor());
            reel.setCreatedBy(reeldata.getCreatedBy());
            reel.setInitialWeight(reeldata.getInitialWeight());
            reel.setQuality(reeldata.getQuality());
            reel.setCurrentWeight(reeldata.getInitialWeight());
            reel.setSupplierName(reeldata.getSupplierName());
            reel.setUnit(reeldata.getUnit());
            reel.setPaperType(reeldata.getPaperType());
            reel.setSize(reeldata.getSize());
            reel.setWidth(reeldata.getWidth());
            reel.setCreatedAt(LocalDate.now());

            reelRepository.save(reel);

            String datestring = reel.getCreatedAt().toString().replaceAll("-", "");

            String barcodeId = "REEL-" + reel.getId() + datestring;
            reel.setBarcodeId(barcodeId);

            byte[] barcodeImage = BarcodeGenerator.generateBarcodeImage(barcodeId);
            reel.setBarcodeImage(barcodeImage);

            reelRepository.save(reel);

            return "Reel registered with Id: " + barcodeId;

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to register reel: " + e.getMessage();
        }
    }
}
