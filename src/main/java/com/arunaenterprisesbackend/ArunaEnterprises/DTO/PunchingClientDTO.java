package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PunchingClientDTO {
    private String client;
    private String product;
    private String size;
    private String ply;
    private double cuttingLength; // Input
    private double deckle;        // Input
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
    // Note: Punching DTO is already complete for input,
    // but the controller logic below will map the 'ups' values
    // directly from deckle/cuttingLength input, or they can be added to this DTO too.
    // For simplicity, the methods below assume they are directly available or derived from deckle/cuttingLength.
}