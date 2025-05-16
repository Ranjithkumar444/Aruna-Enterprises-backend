package com.arunaenterprisesbackend.ArunaEnterprises.DTO;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CalculationDTO {
    private String barcodeId;
    private int width;
    private int height;
    private int length;
    private int ply;
    private int gsm;
    private int noOfBoxMade;
    private String scannedBy;
}
