package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.AttendanceResponseDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.DailyReelUsageDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.DashboardResponseDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Attendance;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AttendanceRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.DailyReelUsageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class DailyReelUsageController {

    private final DailyReelUsageService dailyReelUsageService;
    private final ReelRepository reelRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    public DailyReelUsageController(DailyReelUsageService dailyReelUsageService,ReelRepository reelRepository,
                                    EmployeeRepository employeeRepository,
                                    AttendanceRepository attendanceRepository) {
        this.dailyReelUsageService = dailyReelUsageService;
        this.reelRepository = reelRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
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

    @GetMapping({"/metrics/dashboard"})
    public ResponseEntity<DashboardResponseDTO> getDashboardData(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // --- Reels (reuse same statuses as your other endpoint) ---
        List<ReelStatus> statuses = Arrays.asList(
                ReelStatus.IN_USE,
                ReelStatus.NOT_IN_USE,
                ReelStatus.PARTIALLY_USED_AVAILABLE
        );
        List<Reel> reels = reelRepository.findByStatusIn(statuses);

        // --- Attendance (reproduce mapping you already have) ---
        List<Employee> employees = employeeRepository.findAll();
        List<Attendance> attendanceRecords = attendanceRepository.findByDate(date);

        Map<String, Attendance> attendanceMap = attendanceRecords.stream()
                .collect(Collectors.toMap(
                        Attendance::getBarcodeId,
                        Function.identity(),
                        (existing, duplicate) -> existing // keep first if duplicates exist
                ));

        List<AttendanceResponseDTO> attendanceList = employees.stream()
                .map(employee -> {
                    Attendance attendance = attendanceMap.get(employee.getBarcodeId());
                    boolean isSunday = date.getDayOfWeek() == DayOfWeek.SUNDAY;

                    if (attendance != null) {
                        // convert ZonedDateTime to Asia/Kolkata zone just like your DTO constructor does
                        ZonedDateTime in = attendance.getCheckInTime();
                        ZonedDateTime out = attendance.getCheckOutTime();

                        return new AttendanceResponseDTO(
                                employee.getName(),
                                employee.getBarcodeId(),
                                attendance.getDate(),
                                in != null ? in.withZoneSameInstant(ZoneId.of("Asia/Kolkata")) : null,
                                out != null ? out.withZoneSameInstant(ZoneId.of("Asia/Kolkata")) : null,
                                attendance.getStatus() != null ? attendance.getStatus().toString() : "UNKNOWN",
                                attendance.getRegularHours(),
                                attendance.getOvertimeHours(),
                                attendance.getDaySalary(),
                                isSunday
                        );
                    } else {
                        return new AttendanceResponseDTO(
                                employee.getName(),
                                employee.getBarcodeId(),
                                date,
                                null,
                                null,
                                "ABSENT",
                                0.0,
                                0.0,
                                0.0,
                                isSunday
                        );
                    }
                })
                .collect(Collectors.toList());

        DashboardResponseDTO combined = new DashboardResponseDTO(reels, attendanceList);
        return ResponseEntity.ok(combined);
    }
}