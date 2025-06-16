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
    private String barcodeId;
    private int initialWeight;
    private int currentWeight;
    private Long reelNo;
    private int gsm;
    private int deckle;
    private int burstFactor;
    private String SupplierName;
    private ReelStatus status;
    private String paperType;
    private int previousWeight;
}
