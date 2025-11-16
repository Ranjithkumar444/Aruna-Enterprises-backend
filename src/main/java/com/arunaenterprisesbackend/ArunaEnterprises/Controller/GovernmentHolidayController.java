package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.GovernmentHoliday;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.GovernmentHolidayRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.GovernmentHolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/holidays")
public class GovernmentHolidayController {

    @Autowired
    private GovernmentHolidayService holidayService;

    @Autowired
    private GovernmentHolidayRepository governmentHolidayRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addHoliday(@RequestBody GovernmentHoliday DTO) {
        if (DTO.getHolidayDate() == null) {
            return ResponseEntity.badRequest().body("holidayDate is required");
        }
        GovernmentHoliday created = holidayService.createHolidayAndPrefill(DTO.getHolidayDate(), DTO.getReason());
        return ResponseEntity.ok(created);
    }

    @GetMapping("/all")
    public ResponseEntity<List<GovernmentHoliday>> getHoliday() {
        List<GovernmentHoliday> governmentHoliday = governmentHolidayRepository.findAll();

        if (governmentHoliday.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(governmentHoliday);
    }
}