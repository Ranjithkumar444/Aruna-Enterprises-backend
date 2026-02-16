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

    private int length;

    private int width;

    private int height;

    private int ply;
}
