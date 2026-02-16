package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Attendance;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.AttendanceStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Salary;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AttendanceRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.GovernmentHolidayRepository;
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

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private GovernmentHolidayRepository holidayRepo;

    @Transactional
    public String markAttendance(String barcodeId) {
        if (barcodeId == null || barcodeId.isEmpty()) {
            throw new IllegalArgumentException("Invalid barcode ID");
        }

        Employee employee = employeeRepository.findByBarcodeId(barcodeId);
        if (employee == null) {
            throw new IllegalArgumentException("No employee found with barcode ID: " + barcodeId);
        }

        ZonedDateTime nowUtc = ZonedDateTime.now(UTC_ZONE);
        LocalDate today = nowUtc.withZoneSameInstant(IST_ZONE).toLocalDate();

        boolean isSunday = today.getDayOfWeek() == DayOfWeek.SUNDAY;
        boolean isGovHoliday = holidayRepo.existsByHolidayDate(today);

        Attendance attendance = attendanceRepository.findByEmployeeAndDate(employee, today);
        Salary salary = salaryRepository.findByEmployeeAndMonthAndYear(
                employee, today.getMonthValue(), today.getYear());

        if (salary == null) {
            Salary previousSalary = salaryRepository.findTopByEmployeeOrderByYearDescMonthDesc(employee);
            if (previousSalary != null) {
                salary = new Salary();
                salary.setEmployee(employee);
                salary.setMonthlyBaseSalary(previousSalary.getMonthlyBaseSalary());
                salary.setWorkingDays(previousSalary.getWorkingDays());
                salary.setOtMultiplierFactor(previousSalary.getOtMultiplierFactor());
                salary.setMonth(today.getMonthValue());
                salary.setYear(today.getYear());
                salary.setOneDaySalary(previousSalary.getOneDaySalary());
                salary.setRegularHoursPerDay(previousSalary.getRegularHoursPerDay());
                salary.setOneHourSalary(previousSalary.getOneHourSalary());
                salary.setOtPerHour(previousSalary.getOtPerHour());
                salary.setTotalSalaryThisMonth(0.0);
                salary.setTotalOvertimeHours(0.0);
                salaryRepository.save(salary);
            } else {
                throw new RuntimeException("No previous salary found for employee: " + employee.getName());
            }
        }

        if (attendance == null) {
            attendance = new Attendance();
            attendance.setEmployee(employee);
            attendance.setDate(today);
            attendance.setBarcodeId(barcodeId);
            attendance.setCheckInTime(nowUtc);
            attendance.setCheckedIn(true);
            attendance.setSunday(isSunday);
            attendance.setGovernmentHoliday(isGovHoliday);

            if (isGovHoliday) {
                attendance.setStatus(AttendanceStatus.OT_GOVT_HOLIDAY);
            } else if (isSunday) {
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
        }

        // Auto-paid holiday → convert to real OT day
        if (!attendance.isCheckedIn()
                && attendance.getStatus() == AttendanceStatus.GOVT_HOLIDAY_AUTO_PAID) {

            attendance.setCheckInTime(nowUtc);
            attendance.setCheckedIn(true);
            attendance.setGovernmentHoliday(true);
            attendance.setBarcodeId(barcodeId);

            Salary s = salaryRepository.findByEmployeeAndMonthAndYear(employee, today.getMonthValue(), today.getYear());
            if (s != null && attendance.getDaySalary() > 0) {
                double subtract = s.getOneDaySalary();
                s.setTotalSalaryThisMonth(s.getTotalSalaryThisMonth() - subtract);
                salaryRepository.save(s);
            }

            attendance.setStatus(AttendanceStatus.OT_GOVT_HOLIDAY);
            attendance.setDaySalary(0.0);
            attendanceRepository.save(attendance);

            return "Checked in successfully (holiday overridden to OT).";
        }

        if (!attendance.isCheckedIn()) {
            return "Already checked out.";
        }
        if (attendance.getCheckInTime() == null) {
            throw new IllegalStateException("Cannot check out, check-in time missing.");
        }

        attendance.setCheckOutTime(nowUtc);
        attendance.setCheckedIn(false);
        calculateDailySalary(employee, attendance);

        return String.format(
                "Checked out successfully! Worked %.2f hours (%.2f regular + %.2f OT). Earned ₹%.2f",
                attendance.getRegularHours() + attendance.getOvertimeHours(),
                attendance.getRegularHours(),
                attendance.getOvertimeHours(),
                attendance.getDaySalary()
        );
    }

    public void calculateDailySalary(Employee employee, Attendance attendance) {

        Salary salary = salaryRepository.findByEmployeeAndMonthAndYear(
                employee,
                attendance.getDate().getMonthValue(),
                attendance.getDate().getYear()
        );

        ZonedDateTime in = attendance.getCheckInTime();
        ZonedDateTime out = attendance.getCheckOutTime();
        LocalDate date = attendance.getDate();

        boolean isSunday = date.getDayOfWeek() == DayOfWeek.SUNDAY;
        boolean isGovHoliday = attendance.isGovernmentHoliday();

        ZonedDateTime shiftStart = shiftStart(date);
        ZonedDateTime shiftEnd = shiftEnd(date, salary.getWorkingDays());

        double totalWorked = Duration.between(in, out).toMinutes() / 60.0;
        double regularHours = 0.0;
        double otHours = 0.0;

        if (isGovHoliday) {
            otHours = totalWorked;
            attendance.setStatus(AttendanceStatus.OT_GOVT_HOLIDAY);
        }

        else if (isSunday) {
            if (salary.getWorkingDays() == 26) {
                otHours = totalWorked;
                attendance.setStatus(AttendanceStatus.OT_SUNDAY);
            } else {
                otHours = totalWorked;
                attendance.setStatus(AttendanceStatus.OT_SUNDAY);
            }
        }

        else {
            if (in.isBefore(shiftStart)) {
                otHours += Duration.between(in, shiftStart).toMinutes() / 60.0;
            }

            if (out.isAfter(shiftEnd)) {
                otHours += Duration.between(shiftEnd, out).toMinutes() / 60.0;
            }

            ZonedDateTime regStart = in.isBefore(shiftStart) ? shiftStart : in;
            ZonedDateTime regEnd = out.isAfter(shiftEnd) ? shiftEnd : out;

            if (regStart.isBefore(regEnd)) {
                regularHours = Duration.between(regStart, regEnd).toMinutes() / 60.0;
            }

            attendance.setStatus(AttendanceStatus.PRESENT);
        }

        double daySalary =
                (regularHours * salary.getOneHourSalary()) +
                        (otHours * salary.getOtPerHour());

        salary.setTotalSalaryThisMonth(salary.getTotalSalaryThisMonth() + daySalary);
        salary.setTotalOvertimeHours(salary.getTotalOvertimeHours() + otHours);
        salaryRepository.save(salary);

        attendance.setRegularHours(regularHours);
        attendance.setOvertimeHours(otHours);
        attendance.setDaySalary(daySalary);
        attendanceRepository.save(attendance);
    }

    private ZonedDateTime shiftStart(LocalDate date) {
        return date.atTime(9, 0).atZone(ZoneId.of("Asia/Kolkata"));
    }

    private ZonedDateTime shiftEnd(LocalDate date, int workingDays) {
        if (workingDays == 26) {
            return date.atTime(21, 0).atZone(ZoneId.of("Asia/Kolkata")); // 9 PM
        } else {
            return date.atTime(17, 30).atZone(ZoneId.of("Asia/Kolkata")); // 5:30 PM
        }
    }

}