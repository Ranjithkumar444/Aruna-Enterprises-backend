package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.DailyOrderSummaryDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.FinancialMetricsDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelUsageHistoryDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.WastageMetricsDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelUsageHistory;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.OrderSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/api/order-summaries")
@RequiredArgsConstructor
public class OrderSummaryController {

    private final OrderSummaryService orderSummaryService;

    @PostMapping("/generate/{date}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> generateDailySummary(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        orderSummaryService.generateDailySummary(date);
        return ResponseEntity.ok("Summary generated for " + date);
    }

    @GetMapping("/daily/{date}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<DailyOrderSummaryDTO> getDailySummary(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(orderSummaryService.getDailySummary(date));
    }

    @GetMapping("/range")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<DailyOrderSummaryDTO>> getSummaryBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(orderSummaryService.getSummaryBetweenDates(start, end));
    }

    @GetMapping("/reel-history")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<ReelUsageHistoryDTO>> getReelUsageHistory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        ZonedDateTime startDateTime = start.atStartOfDay(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime endDateTime = end.plusDays(1).atStartOfDay(ZoneId.of("Asia/Kolkata"));

        List<ReelUsageHistory> history = orderSummaryService.getReelUsageHistory(startDateTime, endDateTime);
        List<ReelUsageHistoryDTO> dtos = history.stream()
                .map(this::mapToDTO)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    private ReelUsageHistoryDTO mapToDTO(ReelUsageHistory history) {
        ReelUsageHistoryDTO dto = new ReelUsageHistoryDTO();
        dto.setId(history.getId());
        dto.setReelNo(history.getReelNo());
        dto.setBarcodeId(history.getBarcodeId());
        dto.setUsedWeight(history.getUsedWeight());
        dto.setUsedAt(history.getUsedAt());
        dto.setUsedBy(history.getUsedBy());
        dto.setReelSet(history.getReelSet());
        dto.setBoxDetails(history.getBoxDetails());
        return dto;
    }

    @GetMapping("/financial-metrics")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<FinancialMetricsDTO> getFinancialMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return ResponseEntity.ok(orderSummaryService.getFinancialMetrics(start, end));
    }

    @GetMapping("/wastage-metrics")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<WastageMetricsDTO> getWastageMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return ResponseEntity.ok(orderSummaryService.getWastageMetrics(start, end));
    }
}