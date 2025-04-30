package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReelDTO {
    private String size;
    private Double weight;
    private Double intitalWeight;
    private String quality;
    private String supplierName;
    private LocalDate arrivalDate;
    private String status;
    private String createdBy;
}
