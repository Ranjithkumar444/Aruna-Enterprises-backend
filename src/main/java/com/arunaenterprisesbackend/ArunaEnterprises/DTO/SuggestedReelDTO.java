package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestedReelDTO {
    private String barcodeId;
    private Long reelNo;
    private String client;
    private String clientNormalizer;
    private String product;
    private String size;
    private String ply;
    private int gsm;
    private String unit;
    private int deckle;
    private double cuttingLength;
    private int topGsm;
    private int linerGsm;
    private int fluteGsm;
    private String madeUpOf;
    private String paperTypeTop;
    private String paperTypeBottom;
    private int currentWeight;
    private String reelSet;
    private ReelStatus status;
}


