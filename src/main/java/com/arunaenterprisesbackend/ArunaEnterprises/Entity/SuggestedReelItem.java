package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestedReelItem {
    private String barcodeId;
    private Long reelNo;
    private Integer gsm;
    private Integer deckle;
    private Integer currentWeight;
    private String unit;
    private ReelStatus status;
    private String reelSet;
}