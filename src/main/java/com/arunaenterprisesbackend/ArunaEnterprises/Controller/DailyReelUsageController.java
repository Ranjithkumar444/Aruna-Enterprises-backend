package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.DailyReelUsageDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.DailyReelUsageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class DailyReelUsageController {

    private final DailyReelUsageService dailyReelUsageService;

    public DailyReelUsageController(DailyReelUsageService dailyReelUsageService) {
        this.dailyReelUsageService = dailyReelUsageService;
    }

    /**
     * Endpoint to retrieve daily reel usage data for shipped orders,
     * showing individual reel usage entries within a specified date range.
     *
     * @param startDate The start date for the report (e.g., "2023-01-01T00:00:00Z").
     * @param endDate   The end date for the report (e.g., "2023-01-01T23:59:59Z").
     * @return A list of DailyReelUsageDTOs.
     */

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/daily-reel-usage")
    public List<DailyReelUsageDTO> getDailyOrderReelUsage(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {

        return dailyReelUsageService.getDailyReelUsagesForShippedOrders(startDate, endDate);
    }
}