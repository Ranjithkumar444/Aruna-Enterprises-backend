package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.SalaryRequestDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.SalaryResponseDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Salary;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.SalaryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SalaryService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SalaryRepository salaryRepository;

    public String saveSalary(SalaryRequestDTO salaryRequest) {
        Employee employee = employeeRepository.findById(salaryRequest.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Salary salary = new Salary();
        salary.setEmployee(employee);
        salary.setMonthlyBaseSalary(salaryRequest.getMonthlyBaseSalary());
        salary.setOtRatePerHour(salaryRequest.getOtMultiplierFactor());
        salary.setWorkingDays(salaryRequest.getWorkingDays());

        LocalDate currentDate = LocalDate.now();
        salary.setMonth(currentDate.getMonthValue());
        salary.setYear(currentDate.getYear());

        double monthBaseSalary = salaryRequest.getMonthlyBaseSalary();
        double oneDaySalary;
        double oneHourSalary;
        double oneHourOtSalary;
        double workingDays = salary.getWorkingDays();

        if (workingDays == 26) {
            oneDaySalary = monthBaseSalary / 26;
            oneHourSalary = oneDaySalary / 12;
        } else {
            oneDaySalary = monthBaseSalary / 30;
            oneHourSalary = oneDaySalary / 8;
        }



        if (salary.getOtRatePerHour() == 1) {
            oneHourOtSalary = oneHourSalary;
        } else {
            oneHourOtSalary = oneHourSalary + (oneHourSalary / 2);
        }

        salary.setOneDaySalary(oneDaySalary);
        salary.setOneHourSalary(oneHourSalary);
        salary.setOtPerHour(oneHourOtSalary);

        salaryRepository.save(salary);

        return "Salary details saved for employee ID: " + employee.getId();
    }

   /* public List<SalaryResponseDTO> getCurrentMonthSalaryForAllEmployees() {
        YearMonth currentMonth = YearMonth.now();
        int month = currentMonth.getMonthValue();
        int year = currentMonth.getYear();

        List<Salary> salaries = salaryRepository.findAllByMonthAndYear(month, year);

        // Only one entry per employee
        Map<Long, Salary> uniqueEmployeeSalary = new HashMap<>();
        for (Salary s : salaries) {
            uniqueEmployeeSalary.putIfAbsent(s.getEmployee().getId(), s);
        }

        return uniqueEmployeeSalary.values()
                .stream()
                .map(SalaryResponseDTO::new)
                .collect(Collectors.toList());
    }
    */

    public List<SalaryResponseDTO> getCurrentMonthSalaryForAllEmployees() {
        YearMonth currentMonth = YearMonth.now();
        int month = currentMonth.getMonthValue();
        int year = currentMonth.getYear();

        // Initialize salaries for all active employees if not exists
        initializeMonthlySalariesIfNeeded(month, year);

        List<Salary> salaries = salaryRepository.findAllByMonthAndYear(month, year);

        return salaries.stream()
                .map(SalaryResponseDTO::new)
                .collect(Collectors.toList());
    }


    @Transactional
    public void initializeMonthlySalariesIfNeeded(int month, int year) {
        List<Employee> activeEmployees = employeeRepository.findByIsActive(true);

        for (Employee employee : activeEmployees) {
            // Check if salary already exists for this month
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
                    salary.setOtRatePerHour(latestSalary.getOtRatePerHour());
                    calculateDerivedSalaryFields(salary);
                }

                salaryRepository.save(salary);
            }
        }
    }

    private void calculateDerivedSalaryFields(Salary salary) {
        double monthBaseSalary = salary.getMonthlyBaseSalary();
        double workingDays = salary.getWorkingDays();
        double otMultiplier = salary.getOtRatePerHour();

        // Calculate one day salary
        double oneDaySalary;
        if (workingDays == 26) {
            oneDaySalary = monthBaseSalary / 26;
        } else {
            oneDaySalary = monthBaseSalary / 30;
        }

        // Calculate one hour salary
        double oneHourSalary;
        if (workingDays == 26) {
            oneHourSalary = oneDaySalary / 12;  // 12-hour work day
        } else {
            oneHourSalary = oneDaySalary / 8;   // 8-hour work day
        }

        // Calculate OT per hour
        double otPerHour;
        if (otMultiplier == 1) {
            otPerHour = oneHourSalary;  // Normal rate
        } else {
            otPerHour = oneHourSalary * 1.5;  // Time-and-a-half (common OT rate)
        }

        // Set all calculated fields
        salary.setOneDaySalary(oneDaySalary);
        salary.setOneHourSalary(oneHourSalary);
        salary.setOtPerHour(otPerHour);
    }

    public List<Salary> getLatestSalariesForAllEmployees() {
        return salaryRepository.findLatestSalaryPerEmployee();
    }

}

