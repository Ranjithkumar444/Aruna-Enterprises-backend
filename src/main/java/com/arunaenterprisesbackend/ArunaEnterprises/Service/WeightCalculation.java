package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.CalculationDTO;
import org.springframework.stereotype.Component;

@Component
public class WeightCalculation {


//    public Double calculateWeight(CalculationDTO calculationDTO,String linerOrFlute) {
//        int ply = calculationDTO.getPly();
//        System.out.println("ply = " + ply);
//
//        if(("flute".equalsIgnoreCase(linerOrFlute))){
//            int numFlute = ply/2;
//            System.out.println("flute numbers + " + numFlute);
//            int temp = fluteLayer(calculationDTO);
//
//            System.out.println("Flute calculation = " + temp);
//
//            int total = temp * numFlute;
//
//            System.out.println("Flute Total =" + total);
//            return (double) (calculationDTO.getNoOfBoxMade() * total);
//        }else{
//            int numLiner = (ply/2)+1;
//            System.out.println("Number of Liner = " + numLiner);
//            int temp = plainLayer(calculationDTO);
//            System.out.println("Liner Calculation =" + temp);
//
//            int total = temp * numLiner;
//            System.out.println("Liner Total =" + total);
//            return (double) (calculationDTO.getNoOfBoxMade() * total);
//        }
//    }
//
//    public int plainLayer(CalculationDTO dto) {
//        int A = 2 * dto.getLength() + 2 * dto.getWidth() + 50;
//        int B = dto.getHeight() + 30;
//
//        double area = (A * B * dto.getGsm());
//        System.out.println(area);
//        return (int) (area / 1_000_000.0);
//    }
//
//    public int fluteLayer(CalculationDTO dto) {
//        int A = 2 * dto.getLength() + 2 * dto.getWidth() + 50;
//        int B = dto.getHeight() + 30;
//
//        double area = (A * B * dto.getGsm());
//
//        System.out.println(area);
//        return (int) ((area / 1_000_000.0) * 1.5);
//    }


    public double calculateFluteWeight(CalculationDTO dto) {
        int fluteLayers = dto.getPly() / 2;

        int A = 2 * dto.getLength() + 2 * dto.getWidth() + 50;
        int B = dto.getWidth() * dto.getHeight() + 30;

        double areaPerBox = A * B; // mm²
        double areaInSquareMeters = areaPerBox / 1_000_000.0;

        double weightPerLayerPerBox = areaInSquareMeters * dto.getGsm() * 1.5;

        return fluteLayers * dto.getNoOfBoxMade() * weightPerLayerPerBox;
    }

    public double calculateLinerWeight(CalculationDTO dto) {
        int linerLayers = (dto.getPly() / 2) + 1;

        int A = 2 * dto.getLength() + 2 * dto.getWidth() + 50;
        int B = dto.getWidth() * dto.getHeight() + 30;

        double areaPerBox = A * B; // mm²
        double areaInSquareMeters = areaPerBox / 1_000_000.0;

        double weightPerLayerPerBox = areaInSquareMeters * dto.getGsm();

        return linerLayers * dto.getNoOfBoxMade() * weightPerLayerPerBox; // in kg
    }

//    public double calculateWeight(CalculationDTO dto, String LinerOrFlute) {
//        int length = dto.getLength(); // mm
//        int width = dto.getWidth();
//        int height = dto.getHeight();
//        int ply = dto.getPly();
//        int gsm = dto.getGsm();
//        int numberOfBoxes = dto.getNoOfBoxMade();
//
//        // Calculate surface area of box (in mm²)
//        double surfaceArea = 2 * ((length * width) + (width * height) + (height * length));
//        double areaInM2 = surfaceArea / 1_000_000.0;
//
//        // Add ~6% flap margin
//        areaInM2 *= 1.06;
//
//        double weightPerBox = 0;
//
//        if (LinerOrFlute.equalsIgnoreCase("flute")) {
//            int fluteLayers = ply / 2;
//            weightPerBox = areaInM2 * gsm * 1.5 * fluteLayers;
//        } else if (LinerOrFlute.equalsIgnoreCase("liner")) {
//            int linerLayers = (ply / 2) + 1;
//            weightPerBox = areaInM2 * gsm * linerLayers;
//        }
//
//        double totalWeight = weightPerBox * numberOfBoxes;
//
//        // Add ~7% wastage
//        totalWeight *= 1.07;
//
//        return totalWeight; // grams
//    }


    public double calculateWeight(CalculationDTO dto, String LinerOrFlute) {
        int length = dto.getLength(); // in mm
        int width = dto.getWidth();   // in mm
        int height = dto.getHeight(); // in mm
        int ply = dto.getPly();
        int gsm = dto.getGsm();
        int numberOfBoxes = dto.getNoOfBoxMade();

        // Calculate surface area of the box: 2*(lw + wh + hl) in mm²
        double surfaceArea = (2 * length + 2 * width + 50) * (height + width + 30);
        double areaInM2 = surfaceArea / 1_000_000.0; // convert mm² to m²

        double weightPerBox = 0;

        if (LinerOrFlute.equalsIgnoreCase("flute")) {
            int fluteLayers = ply / 2;
            weightPerBox = areaInM2 * gsm * 1.5 * fluteLayers; // weight per flute box (g)
        } else if (LinerOrFlute.equalsIgnoreCase("liner")) {
            int linerLayers = (ply / 2); // liner = flute + 1
            weightPerBox = areaInM2 * gsm * linerLayers; // weight per liner box (g)
        }else{
            weightPerBox = areaInM2 * gsm;
        }

        double totalWeight = weightPerBox * numberOfBoxes; // total in grams
        return totalWeight; // still in grams (caller divides by 1000 to get kg)
    }
}

