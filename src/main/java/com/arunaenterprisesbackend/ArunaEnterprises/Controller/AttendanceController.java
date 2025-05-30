package com.arunaenterprisesbackend.ArunaEnterprises.Controller;


import com.arunaenterprisesbackend.ArunaEnterprises.DTO.AttendanceResponseDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Attendance;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AttendanceRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AttendanceController {

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

                    if (attendance != null) {
                        return new AttendanceResponseDTO(
                                employee.getName(),
                                employee.getBarcodeId(),
                                attendance.getDate(),
                                attendance.getCheckInTime(),
                                attendance.getCheckOutTime(),
                                attendance.getStatus().toString()
                        );
                    } else {
                        return new AttendanceResponseDTO(
                                employee.getName(),
                                employee.getBarcodeId(),
                                date,
                                null,
                                null,
                                "ABSENT"
                        );
                    }
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
