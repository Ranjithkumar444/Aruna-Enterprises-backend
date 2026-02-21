package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.Service.PaperPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/paper-price")
@RequiredArgsConstructor
public class PaperPriceController {

    private final PaperPriceService paperPriceService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncPaperPrice() {
        paperPriceService.syncPaperPriceCombinations();
        return ResponseEntity.ok("PaperPrice combinations synced successfully");
    }
}