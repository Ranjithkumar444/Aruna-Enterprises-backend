package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.CalculationDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.PunchingBoxDTO;
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
        String ops = dto.getOps();

        double surfaceArea = 0.0;
        double areaInM2 = 0.0;

        if(ops.equalsIgnoreCase("oneops")){
            surfaceArea = (2 * length + 2 * width + 50) * (height + width + 30);
            areaInM2 = surfaceArea / 1_000_000.0;
        }else{
            surfaceArea = ( (2 * length + 50) + (2 * width + 50)) * (height + width + 30);
            areaInM2 = surfaceArea / 1_000_000.0;
        }

        double weightPerBox = 0;

        if (LinerOrFlute.equalsIgnoreCase("flute")) {
            int fluteLayers = ply / 2;
            weightPerBox = areaInM2 * gsm * 1.5 * fluteLayers;
        } else if (LinerOrFlute.equalsIgnoreCase("liner")) {
            int linerLayers = (ply / 2);
            weightPerBox = areaInM2 * gsm * linerLayers;
        } else if (LinerOrFlute.equalsIgnoreCase("cutting")) {
            weightPerBox = areaInM2 * gsm;
        } else {
            int fluteLayers = ply / 2;
            weightPerBox = areaInM2 * gsm * 1.75 * fluteLayers;
        }

        double totalWeight = weightPerBox * numberOfBoxes;
        return totalWeight;
    }

    public double calculatePunchingWeight(PunchingBoxDTO dto, String paperType) {
        int deckle = dto.getDeckle();
        int length = dto.getCuttingLength();
        int gsm = dto.getGsm();
        int sheets = dto.getNoOfSheets();

        double multiplier;

        paperType = paperType.toLowerCase().trim();

        if (paperType.equals("liner") || paperType.equals("cutting")) {
            multiplier = 1.0;
        } else if (paperType.equals("flute")) {
            multiplier = 1.5;
        } else {
            multiplier = 1.75;
        }

        double weightPerSheetGrams = (deckle * length * gsm * multiplier) / 10_000.0;

        return weightPerSheetGrams * sheets;
    }
}