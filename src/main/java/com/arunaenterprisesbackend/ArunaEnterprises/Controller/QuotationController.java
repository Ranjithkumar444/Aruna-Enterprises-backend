package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.CorrugationQuotationRequest;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.PunchingQuotationRequest;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.QuotationResponse;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/quotation")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;

    // ===============================
    // 🔹 RSC API
    // ===============================
    @PostMapping("/rsc")
    public QuotationResponse calculateRSC(
            @RequestBody CorrugationQuotationRequest request
    ) {
        return quotationService.calculateCorrugation(request);
    }

    // ===============================
    // 🔹 PUNCHING API
    // ===============================
    @PostMapping("/punching")
    public QuotationResponse calculatePunching(
            @RequestBody PunchingQuotationRequest request
    ) {
        return quotationService.calculatePunching(request);
    }
}