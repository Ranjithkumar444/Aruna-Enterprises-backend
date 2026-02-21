package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReelDTO {
    private Long reelNo;
    private int gsm;
    private int burstFactor;
    private int deckle;
    private int initialWeight;
    private String unit;
    private String paperType;
    private String supplierName;
    private String createdBy;
    private String reelSet;
    private String location;
}

