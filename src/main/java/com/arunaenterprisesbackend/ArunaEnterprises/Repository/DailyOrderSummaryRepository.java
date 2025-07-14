package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.DailyOrderSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyOrderSummaryRepository extends JpaRepository<DailyOrderSummary, Long> {
    Optional<DailyOrderSummary> findBySummaryDate(LocalDate date);

    List<DailyOrderSummary> findBySummaryDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT d FROM DailyOrderSummary d WHERE EXTRACT(YEAR FROM d.summaryDate) = ?1 AND EXTRACT(MONTH FROM d.summaryDate) = ?2")
    List<DailyOrderSummary> findByYearAndMonth(int year, int month);

    @Query("SELECT d FROM DailyOrderSummary d WHERE EXTRACT(YEAR FROM d.summaryDate) = ?1")
    List<DailyOrderSummary> findByYear(int year);
}
