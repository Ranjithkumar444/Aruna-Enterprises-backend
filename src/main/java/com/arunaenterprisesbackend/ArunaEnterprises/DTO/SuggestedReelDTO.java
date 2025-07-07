package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStatus;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class SuggestedReelDTO {
    private String barcodeId;
    private Long reelNo;
    private String client, clientNormalizer, product, size, ply;
    private int gsm;
    private String unit;
    private int deckle;
    private double cuttingLength;
    private int topGsm, linerGsm, fluteGsm;
    private String madeUpOf, paperTypeTop, paperTypeBottom;
    private int currentWeight;
    private String reelSet;
    private ReelStatus status;
}


