package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.CorrugationQuotationRequest;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.PaperLayerRequest;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.PunchingQuotationRequest;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.QuotationResponse;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.PaperPrice;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.PaperPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuotationService {

    private final PaperPriceRepository paperPriceRepository;

    private static final int TRIM = 5;   // mm
    private static final int JOINT = 30; // mm

    // ================================
    // 🔹 RSC CORRUGATION
    // ================================
    public QuotationResponse calculateCorrugation(CorrugationQuotationRequest request) {

        // flap = width / 2
        int flap = request.getWidth() / 2;

        // Board Size (mm)
        double boardLength = (request.getLength() + request.getWidth()) * 2 + JOINT + TRIM;
        double boardWidth = request.getHeight() + flap + TRIM;

        return calculateCost(boardLength, boardWidth, request.getLayers(), request.getConversionCost());
    }

    // ================================
    // 🔹 PUNCHING
    // ================================
    public QuotationResponse calculatePunching(PunchingQuotationRequest request) {

        double boardLength = request.getSheetLength();
        double boardWidth = request.getSheetWidth();

        return calculateCost(boardLength, boardWidth, request.getLayers(), request.getConversionCost());
    }

    // ================================
    // 🔥 COMMON COST LOGIC
    // ================================
    private QuotationResponse calculateCost(double length, double width,
                                            List<PaperLayerRequest> layers,
                                            int conversionCost) {

        double totalGsm = 0;
        double weightedPrice = 0;

        for (PaperLayerRequest layer : layers) {

            String normalized = normalizePaperType(layer.getPaperType());

            PaperPrice price = paperPriceRepository
                    .findByPaperTypeNormalizedAndGsmAndBf(
                            normalized,
                            layer.getGsm(),
                            layer.getBf()
                    )
                    .orElseThrow(() -> new RuntimeException(
                            "Price not found for: " + normalized +
                                    " GSM: " + layer.getGsm() +
                                    " BF: " + layer.getBf()
                    ));

            totalGsm += layer.getGsm();
            weightedPrice += price.getPricePerKg();
        }

        // avg price/kg
        double avgPricePerKg = weightedPrice / layers.size();

        // weight per sqm (kg)
        double weightPerSqm = totalGsm / 1000.0;

        // cost per sqm
        double costPerSqm = weightPerSqm * avgPricePerKg;

        // add conversion
        costPerSqm += conversionCost;

        // area (convert mm → meter)
        double area = (length / 1000.0) * (width / 1000.0);

        double finalCost = area * costPerSqm;

        return new QuotationResponse(finalCost, weightPerSqm, costPerSqm);
    }

    private String normalizePaperType(String paperType) {
        return paperType == null ? null : paperType.trim().toLowerCase();
    }
}