package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttendanceResponseDTO {
    private String name;
    private String barcodeId;
    private LocalDate date;
    private ZonedDateTime checkInTime;
    private ZonedDateTime checkOutTime;
    private String status;
    private double regularHours;
    private double overtimeHours;
    private double daySalary;
    private boolean isSunday;

    public AttendanceResponseDTO(String name, String barcodeId, LocalDate date,
                                 ZonedDateTime checkInTime, ZonedDateTime checkOutTime,
                                 String status) {
        this(name, barcodeId, date, checkInTime, checkOutTime, status,
                0.0, 0.0, 0.0, date.getDayOfWeek() == DayOfWeek.SUNDAY);
    }
}