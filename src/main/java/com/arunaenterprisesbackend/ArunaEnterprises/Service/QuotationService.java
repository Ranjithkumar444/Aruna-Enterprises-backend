package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.QuotationRequest;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.QuotationResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class    QuotationService {

    private static final Map<String, Double> FLUTE_TAKE_UP_FACTORS = new HashMap<>();
    private static final Map<String, Double> SHADE_PRICE_MODIFIERS = new HashMap<>();
    private static final Map<Double, Double> BF_PRICE_MODIFIERS = new HashMap<>();

    static {
        FLUTE_TAKE_UP_FACTORS.put("A", 1.55);
        FLUTE_TAKE_UP_FACTORS.put("B", 1.47);
        FLUTE_TAKE_UP_FACTORS.put("C", 1.41);
        FLUTE_TAKE_UP_FACTORS.put("E", 1.60);
        FLUTE_TAKE_UP_FACTORS.put("F", 1.60);

        // Define price modifiers for different shades.
        // These are example values; use your own real-world data.
        SHADE_PRICE_MODIFIERS.put("White", 1.15); // 15% premium for white
        SHADE_PRICE_MODIFIERS.put("Brown", 1.00); // Base price for brown
        SHADE_PRICE_MODIFIERS.put("Golden Yellow", 1.10); // 10% premium for golden yellow
        SHADE_PRICE_MODIFIERS.put("Grey", 1.05); // 5% premium for grey
        SHADE_PRICE_MODIFIERS.put("Black", 1.20); // 20% premium for black

        // Define price modifiers for different BF values.
        // This is a simplified example; a real-world scenario would use a more complex formula or a price matrix.
        BF_PRICE_MODIFIERS.put(16.0, 1.00);
        BF_PRICE_MODIFIERS.put(18.0, 1.02);
        BF_PRICE_MODIFIERS.put(20.0, 1.05);
        BF_PRICE_MODIFIERS.put(22.0, 1.08);
        BF_PRICE_MODIFIERS.put(25.0, 1.12);
        BF_PRICE_MODIFIERS.put(28.0, 1.15);
        BF_PRICE_MODIFIERS.put(30.0, 1.18);
        BF_PRICE_MODIFIERS.put(32.0, 1.22);
        BF_PRICE_MODIFIERS.put(35.0, 1.25);
        BF_PRICE_MODIFIERS.put(40.0, 1.30);
    }

    public QuotationResponse calculateQuotation(QuotationRequest req) {
        QuotationResponse res = new QuotationResponse();
        StringBuilder notes = new StringBuilder();

        // 1. Determine base board size (mm)
        double baseBoardLength;
        double baseBoardWidth;

        if ("Corrugated RSC".equalsIgnoreCase(req.getBoxType())) {
            double glueFlap = 25; // mm, default for RSC
            double flapAllowance = 5; // mm extra for trimming/folding
            baseBoardLength = ((req.getLength() + req.getWidth()) * 2) + glueFlap;
            baseBoardWidth = req.getHeight() + req.getWidth() / 2 + flapAllowance;
            notes.append("Box type: Corrugated RSC. Glue flap = ").append(glueFlap).append("mm, flap allowance = ").append(flapAllowance).append("mm. ");
        } else if ("Die-Cut".equalsIgnoreCase(req.getBoxType())) {
            baseBoardLength = req.getLength() + req.getWidth() + 40;
            baseBoardWidth = req.getHeight() + req.getWidth() / 2 + 40;
            notes.append("Box type: Die-Cut. Added extra waste for complex cuts. ");
        } else {
            throw new IllegalArgumentException("Unknown box type: " + req.getBoxType());
        }

        // Apply trim allowances from the request
        baseBoardLength += req.getTrimAllowanceLength();
        baseBoardWidth += req.getTrimAllowanceWidth();

        // 2. Board area (sqm) for 1 box
        double boardAreaPerBoxSqm = (baseBoardLength / 1000.0) * (baseBoardWidth / 1000.0);

        // 3. Calculate total weight and cost by iterating through all layers
        double totalWeightPerSqm = 0.0;
        double totalCostPerSqm = 0.0;

        for (QuotationRequest.LayerDetails layer : req.getLayers()) {
            double layerWeightPerSqm = layer.getGsm() / 1000.0;
            double adjustedWeight = layerWeightPerSqm;
            double adjustedPricePerKg = layer.getPricePerKg();

            // Apply flute take-up factor for flute layers
            if (FLUTE_TAKE_UP_FACTORS.containsKey(layer.getFluteType())) {
                double fluteTakeUpFactor = FLUTE_TAKE_UP_FACTORS.get(layer.getFluteType());
                adjustedWeight = layerWeightPerSqm * fluteTakeUpFactor;
                notes.append(String.format("Flute layer %s with GSM %.0f has a take-up factor of %.2f. ",
                        layer.getFluteType(), layer.getGsm(), fluteTakeUpFactor));
            }

            // Apply price modifiers for shade and BF
            if (SHADE_PRICE_MODIFIERS.containsKey(layer.getShade())) {
                adjustedPricePerKg *= SHADE_PRICE_MODIFIERS.get(layer.getShade());
                notes.append(String.format("Shade '%s' applied a price modifier. ", layer.getShade()));
            }

            if (BF_PRICE_MODIFIERS.containsKey(layer.getBf())) {
                adjustedPricePerKg *= BF_PRICE_MODIFIERS.get(layer.getBf());
                notes.append(String.format("BF '%.0f' applied a price modifier. ", layer.getBf()));
            }

            totalWeightPerSqm += adjustedWeight;
            totalCostPerSqm += adjustedWeight * adjustedPricePerKg;
        }

        // 4. Material cost per box (add process waste)
        double materialCost = totalCostPerSqm * boardAreaPerBoxSqm;
        materialCost *= (1 + req.getProcessWastePercentage() / 100.0);

        // 5. Conversion cost per box
        double boxWeightForConversion = totalWeightPerSqm * boardAreaPerBoxSqm;
        double conversionCost = req.getConversionCostPerKg() * boxWeightForConversion;

        // 6. Total cost before margin
        double totalBeforeMargin = materialCost + conversionCost;

        // 7. Unit price with margin
        double finalPrice = totalBeforeMargin * (1 + req.getGrossMarginPercentage() / 100.0);

        // Set Response
        res.setBoardSizeRequiredPerBox(String.format("%.0fmm x %.0fmm", baseBoardLength, baseBoardWidth));
        res.setActualBoardAreaUsedPerBoxSqm(round(boardAreaPerBoxSqm));
        res.setTotalGSM(round(totalWeightPerSqm * 1000));
        res.setBoxWeightKg(round(boxWeightForConversion));
        res.setMaterialCostPerBox(round(materialCost));
        res.setConversionCostPerBox(round(conversionCost));
        res.setTotalCostBeforeMarginPerBox(round(totalBeforeMargin));
        res.setUnitPrice(round(finalPrice));

        notes.append("Board area per box: ").append(round(boardAreaPerBoxSqm)).append(" sqm. ");
        notes.append("Total weight per sqm: ").append(round(totalWeightPerSqm)).append(" kg. ");
        res.setCalculationNotes(notes.toString());

        return res;
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}