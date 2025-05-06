package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReelDTO {

    private int size;
    private int length;
    private int width;
    private int height;
    private int gsm;
    private String quality;
    private int burstFactor;
    private int deckle;
    private int initialWeight;
    private String unit;
    private String paperType;
    private String supplierName;
    private String createdBy;
}

