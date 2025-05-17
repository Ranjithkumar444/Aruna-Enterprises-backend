package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class OrderDTO {
    private String client;
    private String productType;
    private int quantity;
    private String size;
    private String materialGrade;
    private String deliveryAddress;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expectedCompletionDate;
    private String createdBy;
    private String unit;
    private String transportNumber;
}
