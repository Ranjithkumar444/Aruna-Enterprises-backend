package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "government_holidays")
public class GovernmentHoliday {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private LocalDate holidayDate;


    private String reason;


    public GovernmentHoliday() {}
    public GovernmentHoliday(LocalDate holidayDate, String reason) {
        this.holidayDate = holidayDate;
        this.reason = reason;
    }

    public GovernmentHoliday(Object o, LocalDate holiday, String republicDay) {
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getHolidayDate() { return holidayDate; }
    public void setHolidayDate(LocalDate holidayDate) { this.holidayDate = holidayDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}