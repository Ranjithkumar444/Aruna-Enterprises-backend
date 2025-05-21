package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Attendance;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.AttendanceStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Salary;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AttendanceRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.SalaryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private SalaryRepository salaryRepository;

    @Transactional
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
            return "Checked in successfully!";
        } else {
            if (!attendance.isCheckedIn()) {
                return "Already checked out.";
            }

            attendance.setCheckOutTime(LocalDateTime.now());
            attendance.setCheckedIn(false);
            attendanceRepository.save(attendance);

            Duration duration = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
            double totalWorkedHours = duration.toMinutes() / 60.0;

            Salary salary = salaryRepository.findByEmployeeAndMonthAndYear(
                    employee, today.getMonthValue(), today.getYear()
            );
            if (salary == null) {
                return "Salary configuration missing for employee.";
            }

            int workingDays = salary.getWorkingDays();
            double oneHourSalary = salary.getOneHourSalary();
            double otPerHour = salary.getOtPerHour();

            double regularHours = workingDays == 26 ? 12 : 8;
            double halfDayThreshold = workingDays == 26 ? 6.5 : 4.5;

            double daySalary = 0.0;
            double otHours = 0.0;

            if (totalWorkedHours < halfDayThreshold) {
                // Half day salary
                daySalary = totalWorkedHours * oneHourSalary;
            } else if (totalWorkedHours > regularHours) {
                otHours = totalWorkedHours - regularHours;
                daySalary = (regularHours * oneHourSalary) + (otHours * otPerHour);
            } else {
                daySalary = regularHours * oneHourSalary;
            }

            double newMonthlySalary = salary.getTotalSalaryThisMonth() + daySalary;
            double newOtHours = salary.getTotalOvertimeHours() + otHours;

            salary.setTotalSalaryThisMonth(newMonthlySalary);
            salary.setTotalOvertimeHours(newOtHours);

            salaryRepository.save(salary);

            return String.format("Checked out successfully! Worked %.2f hrs. Earned ₹%.2f today. OT: %.2f hrs", totalWorkedHours, daySalary, otHours);
        }
    }

}
