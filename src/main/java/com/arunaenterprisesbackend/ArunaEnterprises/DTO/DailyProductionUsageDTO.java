package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyProductionUsageDTO {
    //    THIS IS FOR DAILY REPORTING PURPOSE
//    ***********************************
    // --- OrderReelUsage Fields ---
    private Long id;
    private double weightConsumed;
    private ZonedDateTime courgationIn;
    private ZonedDateTime courgationOut;
    private String recordedBy;
    private String usageType; // Flute or liner or top
    private int howManyBox;
    private int previousWeight;

    // --- Order Fields ---
    private String client;
    private String productType;   // Ply 3-ply
    private String typeOfProduct; // Corrugated or punching
    private String productName;   // Own product name
    private int quantity;
    private String size;

    // --- Reel Fields (UPDATED) ---
    private String barcodeId;
    private Long reelNo;
    private int gsm;
    private int burstFactor;
    private int deckle;
    private String paperType;
}