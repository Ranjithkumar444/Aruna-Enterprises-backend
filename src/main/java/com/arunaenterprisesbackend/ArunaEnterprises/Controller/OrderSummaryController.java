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


}