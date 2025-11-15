package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.GovernmentHoliday;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.GovernmentHolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/holidays")
public class GovernmentHolidayController {

    @Autowired
    private GovernmentHolidayService holidayService;

    // Example body: { "holidayDate": "2025-11-16", "reason": "Rain holiday" }
    @PostMapping("/add")
    public ResponseEntity<?> addHoliday(@RequestBody GovernmentHoliday DTO) {
        if (DTO.getHolidayDate() == null) {
            return ResponseEntity.badRequest().body("holidayDate is required");
        }
        GovernmentHoliday created = holidayService.createHolidayAndPrefill(DTO.getHolidayDate(), DTO.getReason());
        return ResponseEntity.ok(created);
    }
}