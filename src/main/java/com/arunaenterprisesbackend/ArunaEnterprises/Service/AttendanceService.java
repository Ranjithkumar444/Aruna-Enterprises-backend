package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Attendance;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.AttendanceStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AttendanceRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AttendanceService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    public String markAttendance(String barcodeId) {
        if (barcodeId == null || barcodeId.isEmpty()) {
            throw new IllegalArgumentException("Invalid barcode ID");
        }

        Employee employee = employeeRepository.findByBarcodeId(barcodeId);
        if (employee == null) {
            throw new IllegalArgumentException("No employee found with barcode ID: " + barcodeId);
        }

        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeAndDate(employee, today);

        if (attendance == null) {
            attendance = new Attendance();
            attendance.setEmployee(employee);
            attendance.setDate(today);
            attendance.setBarcodeId(barcodeId);
            attendance.setCheckInTime(LocalDateTime.now());
            attendance.setStatus(AttendanceStatus.PRESENT);
            attendance.setCheckedIn(true);

            attendanceRepository.save(attendance);
            return " Attendance marked successfully!";
        } else {
            if (attendance.isCheckedIn()) {
                attendance.setCheckOutTime(LocalDateTime.now());
                attendance.setCheckedIn(false);
                attendanceRepository.save(attendance);
                return " Checked out successfully!";
            } else {
                return " Already checked out.";
            }
        }
    }
}
