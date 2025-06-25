package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PunchingBoxDTO {
    private String barcodeId;
    private int deckle;
    private int gsm;
    private int noOfSheets;
    private int cuttingLength;
    private String recordedBy;
    private String orderCompleted;
}
