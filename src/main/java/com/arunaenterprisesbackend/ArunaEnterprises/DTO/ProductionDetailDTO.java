package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionDetailDTO {
    private String client;
    private String clientNormalizer;
    private String productType;
    private String typeOfProduct;
    private String size;
    private String ply;
    private int quantity;
    private double deckle;
    private double cuttingLength;
    private String topMaterial;
    private String fluteMaterial;
    private String linerMaterial;
    private int topGsm;
    private int fluteGsm;
    private int linerGsm;
    private int plain;
    private int sheets;
    private double twoUpsDeckle;
    private double threeUpsDeckle;
    private double fourUpsDeckle;

    private int twoUpsPlain;
    private int twoUpsSheets;

    private int threeUpsPlain;
    private int threeUpsSheets;

    private int fourUpsPlain;
    private int fourUpsSheets;

    private double totalTopWeightReq;
    private double totalLinerWeightReq;
    private double totalFluteWeightReq;
}
