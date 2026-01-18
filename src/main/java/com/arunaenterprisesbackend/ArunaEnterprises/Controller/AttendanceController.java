package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.AttendanceResponseDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Attendance;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AttendanceRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.TreeMap;

@RestController
@RequestMapping("/admin")
public class AttendanceController {

        private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

        @Autowired
        private EmployeeRepository employeeRepository;

        @Autowired
        private AttendanceRepository attendanceRepository;

        @GetMapping("/attendance-list")
        public ResponseEntity<List<AttendanceResponseDTO>> getAttendanceByDate(
                        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

                List<Employee> employees = employeeRepository.findAll();
                List<Attendance> attendanceRecords = attendanceRepository.findByDate(date);

                // ✅ Fix: handle duplicate barcode IDs safely
                Map<String, Attendance> attendanceMap = attendanceRecords.stream()
                                .collect(Collectors.toMap(
                                                Attendance::getBarcodeId,
                                                Function.identity(),
                                                (existing, duplicate) -> existing // keep the first one
                                ));

                List<AttendanceResponseDTO> response = employees.stream()
                                .map(employee -> {
                                        try {
                                                Attendance attendance = attendanceMap.get(employee.getBarcodeId());
                                                boolean isSunday = date.getDayOfWeek() == DayOfWeek.SUNDAY;

                                                if (attendance != null) {
                                                        return new AttendanceResponseDTO(
                                                                        employee.getName(),
                                                                        employee.getBarcodeId(),
                                                                        attendance.getDate(),
                                                                        attendance.getCheckInTime(),
                                                                        attendance.getCheckOutTime(),
                                                                        attendance.getStatus() != null
                                                                                        ? attendance.getStatus()
                                                                                                        .toString()
                                                                                        : "UNKNOWN",
                                                                        attendance.getRegularHours(),
                                                                        attendance.getOvertimeHours(),
                                                                        attendance.getDaySalary(),
                                                                        isSunday);
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
                                                                        isSunday);
                                                }
                                        } catch (Exception e) {
                                                // Log for debugging (optional)
                                                System.err.println("Error processing employee: "
                                                                + employee.getBarcodeId());
                                                e.printStackTrace();
                                                throw e;
                                        }
                                })
                                .collect(Collectors.toList());

                return ResponseEntity.ok(response);
        }

        @PostConstruct
        public void logTimeZone() {
                System.out.println("System Zone: " + ZoneId.systemDefault());
                System.out.println("ZonedDateTime.now(): " + ZonedDateTime.now());
                System.out.println("UTC Time: " + ZonedDateTime.now(ZoneOffset.UTC));
                System.out.println(
                                "IST Time: " + ZonedDateTime.now(ZoneOffset.UTC)
                                                .withZoneSameInstant(ZoneId.of("Asia/Kolkata")));
        }

        @GetMapping("/present-today")
        public ResponseEntity<Map<String, List<AttendanceResponseDTO>>> getPresentEmployeesToday(
                        @RequestParam(value = "date", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date) {
                // 1. Get Today's Date or Selected Date in IST
                LocalDate targetDate = (date != null) ? date : LocalDate.now(IST_ZONE);

                // 2. Fetch ONLY attendance records that exist for the target date
                List<Attendance> presentRecords = attendanceRepository.findByDate(targetDate);

                // 3. Stream, Filter, and Group by Unit
                Map<String, List<AttendanceResponseDTO>> response = presentRecords.stream()
                                .filter(a -> a.getCheckInTime() != null) // Only actual check-ins
                                .collect(Collectors.groupingBy(
                                                // Key Mapper: Group by Unit Name
                                                attendance -> (attendance.getEmployee() != null
                                                                && attendance.getEmployee().getUnit() != null)
                                                                                ? attendance.getEmployee().getUnit()
                                                                                : "Unknown Unit",

                                                // Map Factory: Use TreeMap to sort Units alphabetically (Unit A, then
                                                // Unit
                                                // B...)
                                                TreeMap::new,

                                                // Downstream Collector: Convert Attendance Entity to DTO
                                                Collectors.mapping(attendance -> new AttendanceResponseDTO(
                                                                attendance.getEmployee().getName(),
                                                                attendance.getBarcodeId(),
                                                                attendance.getDate(),
                                                                attendance.getCheckInTime() != null
                                                                                ? attendance.getCheckInTime()
                                                                                                .withZoneSameInstant(
                                                                                                                IST_ZONE)
                                                                                : null,
                                                                attendance.getCheckOutTime() != null
                                                                                ? attendance.getCheckOutTime()
                                                                                                .withZoneSameInstant(
                                                                                                                IST_ZONE)
                                                                                : null,
                                                                attendance.getStatus() != null
                                                                                ? attendance.getStatus().toString()
                                                                                : "PRESENT",
                                                                attendance.getRegularHours(),
                                                                attendance.getOvertimeHours(),
                                                                attendance.getDaySalary(),
                                                                attendance.getDate()
                                                                                .getDayOfWeek() == DayOfWeek.SUNDAY),
                                                                Collectors.toList())));

                return ResponseEntity.ok(response);
        }
}
