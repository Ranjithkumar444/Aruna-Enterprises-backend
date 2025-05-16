package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
public class ReelRegistrationResponseDTO {
    private String barcodeId;

    public ReelRegistrationResponseDTO(String barcodeId) {
        this.barcodeId = barcodeId;
    }

    public String getBarcodeId() {
        return barcodeId;
    }

    public void setBarcodeId(String barcodeId) {
        this.barcodeId = barcodeId;
    }
}
