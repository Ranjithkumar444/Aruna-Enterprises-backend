package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.SalaryRequestDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.SalaryResponseDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Salary;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.SalaryRepository;
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

    public List<SalaryResponseDTO> getCurrentMonthSalaryForAllEmployees() {
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


    public List<Salary> getLatestSalariesForAllEmployees() {
        return salaryRepository.findLatestSalaryPerEmployee();
    }

}

