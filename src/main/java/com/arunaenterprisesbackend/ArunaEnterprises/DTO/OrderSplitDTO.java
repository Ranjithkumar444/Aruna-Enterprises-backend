package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderSplitDTO {
    private int firstOrderQuantity;
    private int secondOrderQuantity;
}
