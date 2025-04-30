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

        reelRepository.save(reel);

        String temp = reel.getArrivalDate().toString();
        StringBuilder dateBuilder = new StringBuilder();
        for(int i=0;i<temp.length();i++){
            if(temp.charAt(i) != '-'){
                dateBuilder.append(temp.charAt(i));
            }
        }

        String datestr = dateBuilder.toString();

        String barcodeId = "REEL-" + reel.getId() + datestr;
        reel.setBarcodeId(barcodeId);

        byte[] barcodeImage = BarcodeGenerator.generateBarcodeImage(barcodeId);
        reel.setBarcodeImage(barcodeImage);

        reelRepository.save(reel);

        return "Reel Registered with ID " + barcodeId;
    }

}
