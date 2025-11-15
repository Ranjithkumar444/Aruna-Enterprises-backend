package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AttendanceRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.GovernmentHolidayRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.SalaryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class GovernmentHolidayService {

    @Autowired
    private GovernmentHolidayRepository holidayRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private SalaryRepository salaryRepository;

    /**
     * Adds a government holiday and pre-fills attendance for all active employees (auto-paid).
     */
    @Transactional
    public GovernmentHoliday createHolidayAndPrefill(LocalDate date, String reason) {

        GovernmentHoliday holiday =  holidayRepository.save(new GovernmentHoliday(date, reason));

        List<Employee> employees = employeeRepository.findByIsActive(true);

        for (Employee emp : employees) {

            Salary salary = salaryRepository.findByEmployeeAndMonthAndYear(
                    emp,
                    date.getMonthValue(),
                    date.getYear()
            );

            if (salary == null) continue;

            // ✔ Skip if attendance exists (employee comes on holiday)
            if (attendanceRepository.findByEmployeeAndDate(emp, date) != null) continue;

            Attendance a = new Attendance();
            a.setEmployee(emp);
            a.setDate(date);
            a.setGovernmentHoliday(true);
            a.setStatus(AttendanceStatus.GOVT_HOLIDAY_AUTO_PAID);
            a.setRegularHours(salary.getRegularHoursPerDay());
            a.setOvertimeHours(0);
            a.setDaySalary(salary.getOneDaySalary());
            a.setMonth(date.getMonthValue());
            a.setYear(date.getYear());

            attendanceRepository.save(a);

            // 👉 Add normal salary immediately for both workers
            salary.setTotalSalaryThisMonth(
                    salary.getTotalSalaryThisMonth() + salary.getOneDaySalary()
            );

            salaryRepository.save(salary);
        }

        return holiday;
    }

}