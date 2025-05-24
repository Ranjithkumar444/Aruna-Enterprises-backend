package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.CalculationDTO;
import org.springframework.stereotype.Component;

@Component
public class WeightCalculation {

    public double calculateWeight(CalculationDTO dto, String LinerOrFlute) {
        int length = dto.getLength();
        int width = dto.getWidth();
        int height = dto.getHeight();
        int ply = dto.getPly();
        int gsm = dto.getGsm();
        int numberOfBoxes = dto.getNoOfBoxMade();

        double surfaceArea = (2 * length + 2 * width + 50) * (height + width + 30);
        double areaInM2 = surfaceArea / 1_000_000.0;

        double weightPerBox = 0;

        if (LinerOrFlute.equalsIgnoreCase("flute")) {
            int fluteLayers = ply / 2;
            weightPerBox = areaInM2 * gsm * 1.5 * fluteLayers;
        } else if (LinerOrFlute.equalsIgnoreCase("liner")) {
            int linerLayers = (ply / 2);
            weightPerBox = areaInM2 * gsm * linerLayers;
        }else{
            weightPerBox = areaInM2 * gsm;
        }

        double totalWeight = weightPerBox * numberOfBoxes;
        return totalWeight;
    }
}

