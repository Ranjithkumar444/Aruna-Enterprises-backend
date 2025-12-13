package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReelCalculationAutoFillDTO {

    private int deckle;

    private int gsm;

    private int cuttingLength;

    private double length;

    private double width;

    private double height;

    private int ply;
}
