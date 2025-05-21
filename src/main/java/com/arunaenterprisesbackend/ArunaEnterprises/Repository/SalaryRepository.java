package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByEmployeeId(Long employeeId);
    Optional<Salary> findById(Long employeeId);
    Salary findByEmployeeAndMonthAndYear(Employee employee, int month, int year);

    @Query("SELECT s FROM Salary s WHERE s.month = :month AND s.year = :year")
    List<Salary> findAllByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query(value = """
    SELECT s.*
    FROM salary s
    INNER JOIN (
        SELECT employee_id, MAX(CONCAT(year, LPAD(month, 2, '0'))) as max_ym
        FROM salary
        GROUP BY employee_id
    ) latest ON CONCAT(s.year, LPAD(s.month, 2, '0')) = latest.max_ym
              AND s.employee_id = latest.employee_id
    """, nativeQuery = true)
    List<Salary> findLatestSalaryPerEmployee();

}

