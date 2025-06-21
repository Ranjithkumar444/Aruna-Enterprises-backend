package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderUsageDetailsDTO {
    private String client;
    private String productType;
    private Integer quantity;
    private String size;
    private Double weightConsumed;
    private ZonedDateTime courgationIn;
    private ZonedDateTime courgationOut;
    private String unit;
    private Integer howManyBox;
}