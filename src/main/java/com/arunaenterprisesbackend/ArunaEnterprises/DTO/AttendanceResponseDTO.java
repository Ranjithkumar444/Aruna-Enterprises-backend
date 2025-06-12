package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttendanceResponseDTO {
    private String name;
    private String barcodeId;
    private LocalDate date;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status;
    private double regularHours;
    private double overtimeHours;
    private double daySalary;
    private boolean isSunday;

    public AttendanceResponseDTO(String name, String barcodeId, LocalDate date,
                                 LocalDateTime checkInTime, LocalDateTime checkOutTime,
                                 String status) {
        this(name, barcodeId, date, checkInTime, checkOutTime, status,
                0.0, 0.0, 0.0, date.getDayOfWeek() == DayOfWeek.SUNDAY);
    }
}
