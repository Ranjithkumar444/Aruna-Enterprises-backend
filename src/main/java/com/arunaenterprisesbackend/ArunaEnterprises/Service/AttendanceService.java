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
        boolean isSunday = today.getDayOfWeek() == DayOfWeek.SUNDAY;
        Attendance attendance = attendanceRepository.findByEmployeeAndDate(employee, today);
        Salary salary = salaryRepository.findByEmployeeAndMonthAndYear(
                employee, today.getMonthValue(), today.getYear());

        if (attendance == null) {
            // Check in
            attendance = new Attendance();
            attendance.setEmployee(employee);
            attendance.setDate(today);
            attendance.setBarcodeId(barcodeId);
            attendance.setCheckInTime(LocalDateTime.now());

            if (isSunday) {
                // For 30-day workers: Deduct auto-paid salary and convert to OT
                if (salary.getWorkingDays() == 30) {
                    salary.setTotalSalaryThisMonth(
                            salary.getTotalSalaryThisMonth() - salary.getOneDaySalary());
                }
                attendance.setStatus(AttendanceStatus.OT_SUNDAY);
            } else {
                attendance.setStatus(AttendanceStatus.PRESENT);
            }
            attendance.setCheckedIn(true);
            attendance.setSunday(isSunday);

            attendanceRepository.save(attendance);
            return "Checked in successfully!";
        } else {
            if (!attendance.isCheckedIn()) {
                return "Already checked out.";
            }

            // Check out
            attendance.setCheckOutTime(LocalDateTime.now());
            attendance.setCheckedIn(false);
            attendanceRepository.save(attendance);

            calculateDailySalary(employee, attendance);

            return String.format("Checked out successfully! Worked %.2f hours (%.2f regular + %.2f OT). Earned ₹%.2f",
                    attendance.getRegularHours() + attendance.getOvertimeHours(),
                    attendance.getRegularHours(),
                    attendance.getOvertimeHours(),
                    attendance.getDaySalary());
        }
    }

    public void calculateDailySalary(Employee employee, Attendance attendance) {
        LocalDate date = attendance.getDate();
        boolean isSunday = date.getDayOfWeek() == DayOfWeek.SUNDAY;

        Salary salary = salaryRepository.findByEmployeeAndMonthAndYear(
                employee, date.getMonthValue(), date.getYear()
        );
        if (salary == null) {
            throw new RuntimeException("Salary configuration missing for employee.");
        }

        if (isSunday && salary.getWorkingDays() == 30 && attendance.getCheckInTime() == null) {
            double normalDaySalary = salary.getOneDaySalary();

            salary.setTotalSalaryThisMonth(salary.getTotalSalaryThisMonth() + normalDaySalary);
            salaryRepository.save(salary);

            attendance.setStatus(AttendanceStatus.ABSENT);
            attendance.setRegularHours(0.0);
            attendance.setOvertimeHours(0.0);
            attendance.setDaySalary(normalDaySalary);
            attendanceRepository.save(attendance);
            return;
        }

        Duration duration = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
        double totalWorkedHours = duration.toMinutes() / 60.0;

        double regularHoursPerDay = salary.getRegularHoursPerDay();
        double oneHourSalary = salary.getOneHourSalary();
        double otPerHour = salary.getOtPerHour();
        int workingDays = salary.getWorkingDays();

        double daySalary = 0.0;
        double otHours = 0.0;
        double regularHours = 0.0;

        if (workingDays == 26) {
            if (isSunday) {
                // All Sunday hours are OT for 26-day workers
                otHours = totalWorkedHours;
                daySalary = otHours * otPerHour;
            } else {
                if (totalWorkedHours <= regularHoursPerDay) {
                    regularHours = totalWorkedHours;
                    daySalary = regularHours * oneHourSalary;
                } else {
                    regularHours = regularHoursPerDay;
                    otHours = totalWorkedHours - regularHoursPerDay;
                    daySalary = (regularHours * oneHourSalary) + (otHours * otPerHour);
                }
            }
        } else {
            // 30-day workers
            if (isSunday) {
                // All Sunday hours are OT
                otHours = totalWorkedHours;
                daySalary = otHours * otPerHour;
            } else {
                // Paid full day even if worked less
                regularHours = regularHoursPerDay;
                daySalary = regularHours * oneHourSalary;

                if (totalWorkedHours > regularHoursPerDay) {
                    otHours = totalWorkedHours - regularHoursPerDay;
                    daySalary += otHours * otPerHour;
                }
            }
        }

        // Update salary record
        salary.setTotalSalaryThisMonth(salary.getTotalSalaryThisMonth() + daySalary);
        salary.setTotalOvertimeHours(salary.getTotalOvertimeHours() + otHours);
        salaryRepository.save(salary);

        // Update attendance with calculated values
        attendance.setRegularHours(regularHours);
        attendance.setOvertimeHours(otHours);
        attendance.setDaySalary(daySalary);
        attendanceRepository.save(attendance);
    }
}
