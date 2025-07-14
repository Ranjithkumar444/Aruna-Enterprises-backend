package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.Data;

@Data
public class OrderSplitDTO {
    private Long originalOrderId;
    private int firstOrderQuantity;
    private int secondOrderQuantity;
}
