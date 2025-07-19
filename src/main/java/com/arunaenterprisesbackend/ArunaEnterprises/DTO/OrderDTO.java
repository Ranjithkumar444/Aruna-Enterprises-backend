package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderDTO {
    private String client;
    private String productType;
    private String typeOfProduct;
    private String productName;
    private int quantity;
    private String size;
    private String deliveryAddress;
    private String status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime expectedCompletionDate;
    private String createdBy;
    private String unit;
    private String transportNumber;
}
