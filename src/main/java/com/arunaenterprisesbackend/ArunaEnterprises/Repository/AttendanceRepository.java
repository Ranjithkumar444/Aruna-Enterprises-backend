package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Attendance;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance,Long> {
    Attendance findByEmployeeAndDate(Employee employee, LocalDate date);

    List<Attendance> findAllByEmployee(Employee employee);

    List<Attendance> findAllByDate(LocalDate date);

    Attendance findByBarcodeIdAndDate(String barcodeId, LocalDate date);

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Attendance a WHERE a.employee = :employee AND a.date BETWEEN :start AND :end")
    List<Attendance> findByEmployeeAndDateBetween(
            @Param("employee") Employee employee,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee = :employee AND a.date BETWEEN :start AND :end")
    long countByEmployeeAndDateBetween(
            @Param("employee") Employee employee,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT a FROM Attendance a WHERE a.employee.barcodeId = :barcodeId AND a.date BETWEEN :start AND :end ORDER BY a.date")
    List<Attendance> findByBarcodeIdAndDateRange(
            @Param("barcodeId") String barcodeId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    // Replace this problematic method
    @Query("SELECT a.checkInTime FROM Attendance a WHERE a.employee.id = :employeeId AND MONTH(a.checkInTime) = :month AND YEAR(a.checkInTime) = :year")
    List<LocalDateTime> findByEmployeeAndMonthAndYear(@Param("employeeId") Long employeeId,
                                                      @Param("month") int month,
                                                      @Param("year") int year);

    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId AND a.month = :month AND a.year = :year")
    List<Attendance> findByEmployeeIdAndMonthAndYear(
            @Param("employeeId") Long employeeId,
            @Param("month") int month,
            @Param("year") int year
    );

    @Modifying
    @Query("DELETE FROM Attendance a WHERE a.employee IN :employees")
    void deleteAllByEmployees(@Param("employees") List<Employee> employees);

    Optional<Attendance> findByEmployeeIdAndDate(long id, LocalDate date);
}
