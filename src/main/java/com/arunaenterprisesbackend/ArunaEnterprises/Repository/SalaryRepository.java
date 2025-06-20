package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByEmployeeId(Long employeeId);
    Optional<Salary> findById(Long employeeId);

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

    @Query("SELECT s FROM Salary s WHERE s.employee = :employee ORDER BY s.year DESC, s.month DESC")
    List<Salary> findByEmployeeOrderByYearDescMonthDesc(@Param("employee") Employee employee);

    @Query("SELECT s FROM Salary s WHERE s.employee = :employee ORDER BY s.year DESC, s.month DESC LIMIT 1")
    Salary findTopByEmployeeOrderByYearDescMonthDesc(@Param("employee") Employee employee);

    @Query("SELECT s FROM Salary s WHERE s.employee.id = :employeeId ORDER BY s.year DESC, s.month DESC LIMIT 1")
    Salary findLatestByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT s FROM Salary s WHERE s.employee = :employee AND s.month = :month AND s.year = :year")
    Salary findByEmployeeAndMonthAndYear(
            @Param("employee") Employee employee,
            @Param("month") int month,
            @Param("year") int year);

    @Modifying
    @Query("DELETE FROM Salary s WHERE s.employee IN :employees")
    void deleteAllByEmployees(@Param("employees") List<Employee> employees);

    Optional<Salary> findByEmployeeIdAndMonthAndYear(long id, int monthValue, int year);
}