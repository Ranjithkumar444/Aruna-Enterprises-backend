package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyReelUsageDTO {
    private Long orderReelUsageId; // The ID of the specific usage entry
    private Long orderId;
    private String clientName; // From Order
    private String productType; // From Order
    private String typeOfProduct;
    private String productName;
    private ZonedDateTime orderCreatedDate; // From Order
    private ZonedDateTime courgationIn; // Date when reel usage started for this entry
    private ZonedDateTime courgationOut; // Date when reel usage finished for this entry
    private Long reelId;
    private int quantity;
    private int deckle;
    private int gsm;
    private int bf;
    private String unit;

    private String reelBarcodeId; // From Reel
    private int reelNo; // From Reel
    private String paperType; // From Reel
    private double initialWeight; // From Reel
    private double weightConsumed; // Weight consumed by this specific OrderReelUsage entry
    private double currentWeight;
    private int previousWeight; // From OrderReelUsage (note: type is int here as per your entity)
    private String usageType; // From OrderReelUsage
    private String recordedBy; // From OrderReelUsage
}