package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStatus;
import jakarta.persistence.Lob;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReelResponseDTO {

    private int initialWeight;
    private int currentWeight;
    private int gsm;
    private int deckle;
    private int burstFactor;
    private String SupplierName;
    private ReelStatus status;
    private String paperType;
    @Lob
    private byte[] barcodeImage;
}
