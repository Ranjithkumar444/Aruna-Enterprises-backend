package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.Barcode;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Attendance;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.AttendanceStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AttendanceRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@CrossOrigin("*")
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @GetMapping("/greet")
    public String HelloController(){
        return "HEllo World";
    }

    @PostMapping("/attendance-scan")
    public String attendanceScan(@RequestBody  Barcode barcodeId){
        System.out.println(barcodeId.getBarcodeId());
        return barcodeId.getBarcodeId();
    }

    @PostMapping("/scan-attendance")
    public String scanAttendance(@RequestBody Barcode barcodeId){
        Employee employee = employeeRepository.findByBarcodeId(barcodeId.getBarcodeId());

        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeAndDate(employee, today);

        if (attendance == null) {
            attendance = new Attendance();
            attendance.setEmployee(employee);
            attendance.setDate(today);
            attendance.setBarcodeId(barcodeId.getBarcodeId());
            attendance.setCheckInTime(LocalDateTime.now());
            attendance.setStatus(AttendanceStatus.PRESENT);
            attendance.setCheckedIn(true);

            attendanceRepository.save(attendance);
            return "Attendance marked successfully!";
        }else{
            if(attendance.isCheckedIn()){
                attendance.setCheckOutTime(LocalDateTime.now());
                attendance.setCheckedIn(false);
                attendanceRepository.save(attendance);

                return "Checkout seccessfully!";
            }else{
                return "Already checkout out";
            }
        }
    }

}
