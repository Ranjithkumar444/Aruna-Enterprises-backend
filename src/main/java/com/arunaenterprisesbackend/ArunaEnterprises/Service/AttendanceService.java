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

import java.time.*;

@Service
public class AttendanceService {

    private static final ZoneId UTC_ZONE = ZoneOffset.UTC;
    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

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

        ZonedDateTime nowUtc = ZonedDateTime.now(UTC_ZONE); // ✅ use UTC
        LocalDate today = nowUtc.withZoneSameInstant(IST_ZONE).toLocalDate();

        boolean isSunday = today.getDayOfWeek() == DayOfWeek.SUNDAY;
        Attendance attendance = attendanceRepository.findByEmployeeAndDate(employee, today);
        Salary salary = salaryRepository.findByEmployeeAndMonthAndYear(
                employee, today.getMonthValue(), today.getYear());

        if (salary == null) {
            throw new RuntimeException("Salary configuration missing for employee for " + today.getMonth() + " " + today.getYear());
        }

        if (attendance == null) {
            // Check in
            attendance = new Attendance();
            attendance.setEmployee(employee);
            attendance.setDate(today);
            attendance.setBarcodeId(barcodeId);
            attendance.setCheckInTime(nowUtc);
            attendance.setCheckedIn(true);
            attendance.setSunday(isSunday);

            if (isSunday) {
                if (salary.getWorkingDays() == 30) {
                    salary.setTotalSalaryThisMonth(salary.getTotalSalaryThisMonth() - salary.getOneDaySalary());
                    salaryRepository.save(salary);
                }
                attendance.setStatus(AttendanceStatus.OT_SUNDAY);
            } else {
                attendance.setStatus(AttendanceStatus.PRESENT);
            }

            attendanceRepository.save(attendance);
            return "Checked in successfully!";
        } else {
            // Check out
            if (!attendance.isCheckedIn()) {
                return "Already checked out.";
            }
            if (attendance.getCheckInTime() == null) {
                throw new IllegalStateException("Cannot check out, check-in time is missing for attendance record ID: " + attendance.getId());
            }

            attendance.setCheckOutTime(nowUtc);
            attendance.setCheckedIn(false);
            calculateDailySalary(employee, attendance);

            return String.format("Checked out successfully! Worked %.2f hours (%.2f regular + %.2f OT). Earned ₹%.2f",
                    attendance.getRegularHours() + attendance.getOvertimeHours(),
                    attendance.getRegularHours(),
                    attendance.getOvertimeHours(),
                    attendance.getDaySalary());
        }
    }

    public void calculateDailySalary(Employee employee, Attendance attendance) {
        if (attendance.getCheckInTime() == null || attendance.getCheckOutTime() == null) {
            throw new IllegalArgumentException("Check-in or Check-out time is missing for attendance ID: " + attendance.getId());
        }

        ZonedDateTime checkIn = attendance.getCheckInTime();
        ZonedDateTime checkOut = attendance.getCheckOutTime();

        LocalDate date = attendance.getDate();
        boolean isSunday = date.getDayOfWeek() == DayOfWeek.SUNDAY;

        Salary salary = salaryRepository.findByEmployeeAndMonthAndYear(
                employee, date.getMonthValue(), date.getYear());
        if (salary == null) {
            throw new RuntimeException("Salary configuration missing for employee.");
        }

        Duration duration = Duration.between(checkIn, checkOut);
        double totalWorkedHours = duration.toMinutes() / 60.0;
        if (totalWorkedHours < 0) {
            totalWorkedHours = 0;
            System.err.println("Warning: Negative worked hours calculated for attendance ID: " + attendance.getId());
        }

        double oneHourSalary = salary.getOneHourSalary();
        double otPerHour = salary.getOtPerHour();
        int workingDays = salary.getWorkingDays();

        double daySalary = 0.0;
        double otHours = 0.0;
        double regularHours = 0.0;

        if (isSunday) {
            otHours = totalWorkedHours;
            daySalary = otHours * otPerHour;
            attendance.setStatus(AttendanceStatus.OT_SUNDAY);
        } else {
            if (workingDays == 26) {
                if (totalWorkedHours <= 12.0) {
                    regularHours = totalWorkedHours;
                    daySalary = regularHours * oneHourSalary;
                } else {
                    regularHours = 12.0;
                    otHours = totalWorkedHours - 12.0;
                    daySalary = (regularHours * oneHourSalary) + (otHours * otPerHour);
                }
            } else { // 30-day workers on a weekday
                if (totalWorkedHours <= 8.5) {
                    regularHours = totalWorkedHours;
                    daySalary = regularHours * oneHourSalary;
                } else {
                    regularHours = 8.5;
                    otHours = totalWorkedHours - 8.5;
                    daySalary = (regularHours * oneHourSalary) + (otHours * otPerHour);
                }
            }
            attendance.setStatus(AttendanceStatus.PRESENT);
        }

        salary.setTotalSalaryThisMonth(salary.getTotalSalaryThisMonth() + daySalary);
        salary.setTotalOvertimeHours(salary.getTotalOvertimeHours() + otHours);
        salaryRepository.save(salary);

        attendance.setRegularHours(regularHours);
        attendance.setOvertimeHours(otHours);
        attendance.setDaySalary(daySalary);
        attendanceRepository.save(attendance);
    }
}
