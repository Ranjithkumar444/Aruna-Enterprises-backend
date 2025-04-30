package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Utility.BarcodeGenerator;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.CalculationDTO;
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
        reel.setIntitalWeight(reeldata.getIntitalWeight());
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

    public String toggleReelStatus(String barcodeId) {
        if (barcodeId == null || barcodeId.isEmpty()) {
            throw new IllegalArgumentException("Barcode ID is missing");
        }

        Reel reel = reelRepository.findByBarcodeId(barcodeId);
        if (reel == null) {
            throw new IllegalArgumentException("Reel not found for barcode: " + barcodeId);
        }

        if (reel.getStatus() == ReelStatus.IN_USE) {
            reel.setStatus(ReelStatus.NOT_IN_USE);
        } else if (reel.getStatus() == ReelStatus.NOT_IN_USE) {
            reel.setStatus(ReelStatus.IN_USE);
        } else {
            return "⚠️ Reel is expired or unusable.";
        }

        reelRepository.save(reel);
        return "Reel status updated to: " + reel.getStatus();
    }

    public String calculateAndReduceWeight(CalculationDTO dto) {
        Reel reel = reelRepository.findByBarcodeId(dto.getBarcodeId());
        if (reel == null) {
            throw new IllegalArgumentException("Reel not found for barcode: " + dto.getBarcodeId());
        }

        double weightPerBox;
        switch (dto.getBoxType().toLowerCase()) {
            case "normal":
                weightPerBox = 0.300;
                break;
            case "medium":
                weightPerBox = 0.400;
                break;
            case "heavy":
                weightPerBox = 0.500;
                break;
            default:
                throw new IllegalArgumentException("Invalid box type: " + dto.getBoxType());
        }

        double usedWeight = dto.getNumberOfBoxes() * weightPerBox;
        if(reel.getStatus() == ReelStatus.NOT_IN_USE){
            return "the reel is not set to use";
        }

        if(reel.getStatus() == ReelStatus.IN_USE){
            reel.setStatus(ReelStatus.NOT_IN_USE);
        }

        if (reel.getWeight() == null || reel.getWeight() < usedWeight) {
            throw new IllegalArgumentException("Not enough reel weight available.");
        }

        if(reel.getWeight() - usedWeight <= 5){
            reel.setStatus(ReelStatus.USE_COMPLETED);
        }

        reel.setWeight(reel.getWeight() - usedWeight);
        reelRepository.save(reel);

        return "✅ Weight reduced by " + usedWeight + " kg. Remaining weight: " + reel.getWeight();
    }
}
