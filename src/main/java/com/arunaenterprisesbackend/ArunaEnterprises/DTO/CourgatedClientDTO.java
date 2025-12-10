package com.arunaenterprisesbackend.ArunaEnterprises.DTO;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CourgatedClientDTO {
    private String client;
    private String product;
    private String size;
    private String ply;
    private double cuttingLength; // Now input
    private double deckle;        // Now input
    private int topGsm;
    private int linerGsm;
    private int fluteGsm;
    private String madeUpOf;
    private String paperTypeTop;
    private String paperTypeBottom;
    private String paperTypeFlute;
    private String description;

    private double sellingPricePerBox;
    private double productionCostPerBox;

    private String fluteType;

    // Fields that were calculated, now expected from user input
    private double oneUps;
    private double twoUps;
    private double threeUps;
    private double fourUps;
    private double fiveUps;
    private double sixUps;
    private double cuttingLengthOneUps;
    private double cuttingLengthTwoUps;
    private String piece;
}
