package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.SalaryMetricsDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaryMetricsService {

    @Autowired
    private SalaryRepository salaryRepository;

    public List<SalaryMetricsDTO> getUnitWiseMetrics() {
        return salaryRepository.getUnitWiseSalaryMetrics();
    }

    public List<SalaryMetricsDTO> getEmployeeWiseMetrics() {
        return salaryRepository.getSalaryMetrics();
    }

    public List<SalaryMetricsDTO> getUnitWiseSalaryByMonth(int month, int year) {
        return salaryRepository.getUnitWiseSalaryByMonthAndYear(month, year);
    }

    public List<SalaryMetricsDTO> getEmployeeWiseSalaryByMonth(int month, int year) {
        return salaryRepository.getEmployeeWiseSalaryByMonthAndYear(month, year);
    }
}
