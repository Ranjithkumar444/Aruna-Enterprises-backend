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

        Map<String, Attendance> attendanceMap = attendanceRecords.stream()
                .collect(Collectors.toMap(Attendance::getBarcodeId, Function.identity()));

        List<AttendanceResponseDTO> response = employees.stream()
                .map(employee -> {
                    Attendance attendance = attendanceMap.get(employee.getBarcodeId());
                    boolean isSunday = date.getDayOfWeek() == DayOfWeek.SUNDAY;

                    if (attendance != null) {
                        return new AttendanceResponseDTO(
                                employee.getName(),
                                employee.getBarcodeId(),
                                attendance.getDate(),
                                attendance.getCheckInTime(),
                                attendance.getCheckOutTime(),
                                attendance.getStatus().toString(),
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

        return ResponseEntity.ok(response);
    }

    @PostConstruct
    public void logTimeZone() {
        System.out.println("System Zone: " + ZoneId.systemDefault());
        System.out.println("ZonedDateTime.now(): " + ZonedDateTime.now());
        System.out.println("UTC Time: " + ZonedDateTime.now(ZoneOffset.UTC));
        System.out.println("IST Time: " + ZonedDateTime.now(ZoneOffset.UTC).withZoneSameInstant(ZoneId.of("Asia/Kolkata")));
    }

}
