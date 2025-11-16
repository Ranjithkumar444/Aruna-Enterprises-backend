package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.GovernmentHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GovernmentHolidayRepository extends JpaRepository<GovernmentHoliday, Long> {
    boolean existsByHolidayDate(LocalDate date);
    List<GovernmentHoliday> findByHolidayDateBetween(LocalDate start, LocalDate end);
    List<GovernmentHoliday> findAll();
}
