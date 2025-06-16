package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "attendance")
@Setter
@Getter
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String barcodeId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDate date;

    private ZonedDateTime checkInTime;
    private ZonedDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    @Column(name = "checked_in")
    private boolean checkedIn;

    private double regularHours;
    private double overtimeHours;
    private double daySalary;
    private boolean isSunday;

    private int month;
    private int year;
}
