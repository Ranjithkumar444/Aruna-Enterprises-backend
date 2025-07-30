package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "production_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToOne(mappedBy = "productionDetail")
    @JsonBackReference
    private Order order;

    private double totalTopWeightReq;
    private double totalLinerWeightReq;
    private double totalFluteWeightReq;

    private double onePieceCuttingLength;
    private double twoPieceCuttingLength;

    private double onePiecePlain;
    private double twoPiecePlain;

    private double onePieceSheet;
    private double twoPieceSheet;
}
