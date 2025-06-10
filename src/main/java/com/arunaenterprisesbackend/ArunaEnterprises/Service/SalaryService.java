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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalaryService {

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

        LocalDate currentDate = LocalDate.now();
        salary.setMonth(currentDate.getMonthValue());
        salary.setYear(currentDate.getYear());

        calculateDerivedSalaryFields(salary);
        salaryRepository.save(salary);

        return "Salary details saved for employee ID: " + employee.getId();
    }

    public void calculateDerivedSalaryFields(Salary salary) {
        double monthBaseSalary = salary.getMonthlyBaseSalary();
        int workingDays = salary.getWorkingDays();
        double otMultiplier = salary.getOtMultiplierFactor();

        // Calculate days in current month
        YearMonth yearMonth = YearMonth.of(salary.getYear(), salary.getMonth());
        int daysInMonth = yearMonth.lengthOfMonth();

        // Calculate one day salary based on actual days in month
        double oneDaySalary = monthBaseSalary / daysInMonth;

        // Set regular hours per day based on working pattern
        double regularHoursPerDay;
        if (workingDays == 26) {
            regularHoursPerDay = 12; // 12-hour work day
        } else {
            regularHoursPerDay = 8;  // 8-hour work day
        }
        salary.setRegularHoursPerDay(regularHoursPerDay);

        // Calculate one hour salary
        double oneHourSalary = oneDaySalary / regularHoursPerDay;

        // Calculate OT per hour
        double otPerHour = oneHourSalary * otMultiplier;

        // Set all calculated fields
        salary.setOneDaySalary(oneDaySalary);
        salary.setOneHourSalary(oneHourSalary);
        salary.setOtPerHour(otPerHour);
    }

    public List<SalaryResponseDTO> getCurrentMonthSalaryForAllEmployees() {
        YearMonth currentMonth = YearMonth.now();
        int month = currentMonth.getMonthValue();
        int year = currentMonth.getYear();

        initializeMonthlySalariesIfNeeded(month, year);

        List<Salary> salaries = salaryRepository.findAllByMonthAndYear(month, year);
        return salaries.stream()
                .map(SalaryResponseDTO::new)
                .collect(Collectors.toList());
    }

//    @Transactional
//    public void initializeMonthlySalariesIfNeeded(int month, int year) {
//        List<Employee> activeEmployees = employeeRepository.findByIsActive(true);
//
//        for (Employee employee : activeEmployees) {
//            if (salaryRepository.findByEmployeeAndMonthAndYear(employee, month, year) == null) {
//                Salary salary = new Salary();
//                salary.setEmployee(employee);
//                salary.setMonth(month);
//                salary.setYear(year);
//                salary.setTotalSalaryThisMonth(0.0);
//                salary.setTotalOvertimeHours(0.0);
//
//                // Copy settings from last month
//                Salary latestSalary = salaryRepository.findTopByEmployeeOrderByYearDescMonthDesc(employee);
//                if (latestSalary != null) {
//                    salary.setMonthlyBaseSalary(latestSalary.getMonthlyBaseSalary());
//                    salary.setWorkingDays(latestSalary.getWorkingDays());
//                    salary.setOtMultiplierFactor(latestSalary.getOtMultiplierFactor());
//                    calculateDerivedSalaryFields(salary);
//                }
//
//                salaryRepository.save(salary);
//            }
//        }
//    }

    @Transactional
    public void initializeMonthlySalariesIfNeeded(int month, int year) {
        List<Employee> activeEmployees = employeeRepository.findByIsActive(true);
        YearMonth yearMonth = YearMonth.of(year, month);
        int totalDaysInMonth = yearMonth.lengthOfMonth();

        // Find all Sundays in the month
        List<LocalDate> sundays = getSundaysInMonth(year, month);

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
    private List<LocalDate> getSundaysInMonth(int year, int month) {
        List<LocalDate> sundays = new ArrayList<>();
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate date = yearMonth.atDay(1);

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