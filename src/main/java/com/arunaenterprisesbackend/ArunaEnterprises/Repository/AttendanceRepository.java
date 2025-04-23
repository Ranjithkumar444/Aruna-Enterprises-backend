package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Attendance;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance,Long> {
    Attendance findByEmployeeAndDate(Employee employee, LocalDate date);

    List<Attendance> findAllByEmployee(Employee employee);

    List<Attendance> findAllByDate(LocalDate date);

    Attendance findByBarcodeIdAndDate(String barcodeId, LocalDate date);

    Page<Attendance> findByDate(LocalDate date, Pageable pageable);
}
