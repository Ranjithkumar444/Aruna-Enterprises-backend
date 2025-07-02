package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.SalaryRequestDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.SalaryResponseDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Attendance;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.AttendanceStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Salary;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AttendanceRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.SalaryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalaryService {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    public String saveSalary(SalaryRequestDTO salaryRequest) {
        Employee employee = employeeRepository.findById(salaryRequest.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Salary salary = new Salary();
        salary.setEmployee(employee);
        salary.setMonthlyBaseSalary(salaryRequest.getMonthlyBaseSalary());
        salary.setOtMultiplierFactor(salaryRequest.getOtMultiplierFactor());
        salary.setWorkingDays(salaryRequest.getWorkingDays());

        ZonedDateTime nowInIST = ZonedDateTime.now(IST_ZONE);
        salary.setMonth(nowInIST.getMonthValue());
        salary.setYear(nowInIST.getYear());

        calculateDerivedSalaryFields(salary);
        salaryRepository.save(salary);

        return "Salary details saved for employee ID: " + employee.getId();
    }

    @Transactional
    public void calculateDerivedSalaryFields(Salary salary) {
        double monthlyBaseSalary = salary.getMonthlyBaseSalary();
        int workingDays = salary.getWorkingDays();
        double otMultiplierFactor = salary.getOtMultiplierFactor();

        double oneDaySalary;
        double regularHoursPerDay;

        if (workingDays == 26) {
            oneDaySalary = monthlyBaseSalary / 26.0;
            regularHoursPerDay = 12.0;
        } else if (workingDays == 30) {
            YearMonth yearMonth = YearMonth.of(salary.getYear(), salary.getMonth());
            int daysInMonth = yearMonth.lengthOfMonth();
            oneDaySalary = monthlyBaseSalary / daysInMonth;
            regularHoursPerDay = 8.0;
        } else {
            throw new IllegalArgumentException("Invalid number of working days: " + workingDays);
        }

        double oneHourSalary = oneDaySalary / regularHoursPerDay;
        double otPerHour = oneHourSalary * otMultiplierFactor;

        salary.setOneDaySalary(oneDaySalary);
        salary.setRegularHoursPerDay(regularHoursPerDay);
        salary.setOneHourSalary(oneHourSalary);
        salary.setOtPerHour(otPerHour);

        // Calculate Sunday salary only if workingDays is 30
        if (workingDays == 30) {
            ZonedDateTime nowInIST = ZonedDateTime.now(IST_ZONE);
            List<LocalDate> sundays = getSundaysInMonth(
                    salary.getYear(),
                    salary.getMonth(),
                    nowInIST.toLocalDate()
            );
            double sundaySalary = oneDaySalary * sundays.size();
            salary.setTotalSalaryThisMonth(sundaySalary);
        }
    }

    public List<SalaryResponseDTO> getCurrentMonthSalaryForAllEmployees() {
        YearMonth currentMonth = YearMonth.now(IST_ZONE);
        int month = currentMonth.getMonthValue();
        int year = currentMonth.getYear();

        initializeMonthlySalariesIfNeeded(month, year);

        List<Salary> salaries = salaryRepository.findAllByMonthAndYear(month, year);
        return salaries.stream()
                .map(SalaryResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void initializeMonthlySalariesIfNeeded(int month, int year) {
        List<Employee> activeEmployees = employeeRepository.findByIsActive(true);
        YearMonth yearMonth = YearMonth.of(year, month);
        int totalDaysInMonth = yearMonth.lengthOfMonth(); // This variable is declared but not used, can be removed if not needed.
        ZonedDateTime nowInIST = ZonedDateTime.now(IST_ZONE);

        // Find all Sundays in the month
        List<LocalDate> sundays = getSundaysInMonth(year, month,null);

        for (Employee employee : activeEmployees) {
            if (salaryRepository.findByEmployeeAndMonthAndYear(employee, month, year) == null) {
                Salary salary = new Salary();
                salary.setEmployee(employee);
                salary.setMonth(month);
                salary.setYear(year);
                salary.setTotalSalaryThisMonth(0.0);
                salary.setTotalOvertimeHours(0.0);

                // Copy settings from last month
                Salary latestSalary = salaryRepository.findTopByEmployeeOrderByYearDescMonthDesc(employee);
                if (latestSalary != null) {
                    salary.setMonthlyBaseSalary(latestSalary.getMonthlyBaseSalary());
                    salary.setWorkingDays(latestSalary.getWorkingDays());
                    salary.setOtMultiplierFactor(latestSalary.getOtMultiplierFactor());
                    calculateDerivedSalaryFields(salary);
                }

                // Pre-add Sunday salaries for 30-day workers
                if (salary.getWorkingDays() == 30) {
                    double sundaySalary = salary.getOneDaySalary() * sundays.size();
                    salary.setTotalSalaryThisMonth(sundaySalary);

                    // Create placeholder attendance records for Sundays
                    sundays.forEach(sunday -> {
                        Attendance attendance = new Attendance();
                        attendance.setEmployee(employee);
                        attendance.setDate(sunday);
                        attendance.setStatus(AttendanceStatus.AUTO_PAID); // New status
                        attendance.setDaySalary(salary.getOneDaySalary());
                        attendanceRepository.save(attendance);
                    });
                }

                salaryRepository.save(salary);
            }
        }
    }

    // Helper method to find Sundays
    private List<LocalDate> getSundaysInMonth(int year, int month, LocalDate startDate) {
        List<LocalDate> sundays = new ArrayList<>();
        YearMonth yearMonth = YearMonth.of(year, month);
        // Default startDate = first day of month if not provided
        LocalDate date = (startDate != null && !startDate.isBefore(yearMonth.atDay(1)))
                ? startDate
                : yearMonth.atDay(1);

        // Iterate until end of month
        while (date.getMonthValue() == month) {
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                sundays.add(date);
            }
            date = date.plusDays(1);
        }
        return sundays;
    }

    public List<Salary> getLatestSalariesForAllEmployees() {
        return salaryRepository.findLatestSalaryPerEmployee();
    }

    @Transactional
    public void calculateAndSaveMonthlySalaryForEmployeetest(Long employeeId, int year, int month) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Salary salary = salaryRepository.findByEmployeeAndMonthAndYear(employee, month, year);
        if (salary == null) {
            throw new RuntimeException("Salary config not found for this month. Please configure it first.");
        }

        // Fetch attendance data
        List<LocalDateTime> punchTimes = attendanceRepository.findByEmployeeAndMonthAndYear(employee.getId(), month, year);

        // Sort punch times and pair check-in/check-out
        punchTimes.sort(Comparator.naturalOrder());

        double totalWorkedHours = 0;
        for (int i = 0; i < punchTimes.size() - 1; i += 2) {
            LocalDateTime in = punchTimes.get(i);
            LocalDateTime out = punchTimes.get(i + 1);
            totalWorkedHours += Duration.between(in, out).toMinutes() / 60.0;
        }

        // Regular hours = working days * regular hours/day
        double expectedRegularHours = salary.getWorkingDays() * salary.getRegularHoursPerDay();
        double overtimeHours = Math.max(0, totalWorkedHours - expectedRegularHours);

        double totalSalary = (expectedRegularHours * salary.getOneHourSalary())
                + (overtimeHours * salary.getOtPerHour());

        salary.setTotalOvertimeHours(overtimeHours);
        salary.setTotalSalaryThisMonth(totalSalary);

        salaryRepository.save(salary);
    }
}