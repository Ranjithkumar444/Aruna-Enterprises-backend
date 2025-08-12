package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.QuotationRequest;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.QuotationResponse;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.QuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class QuotationController {

    @Autowired
    private QuotationService quotationService;

    @PostMapping("/quotation/calculate")
    public QuotationResponse calculateQuotation(@RequestBody QuotationRequest request) {
        return quotationService.calculateQuotation(request);
    }
}
