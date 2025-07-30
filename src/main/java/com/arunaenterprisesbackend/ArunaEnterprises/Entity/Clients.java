package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Clients {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String client;
    private String clientNormalizer;

    private String product;
    private String productNormalizer;
    private String productType;
    private String size;
    private String ply;
    private double deckle;
    private double cuttingLength;
    private int topGsm;
    private int linerGsm;
    private int bottomGsm;
    private int fluteGsm;

    private String madeUpOf;
    private String paperTypeTop;
    private String paperTypeBottom;
    private String paperTypeFlute;

    private String paperTypeTopNorm;
    private String paperTypeBottomNorm;
    private String paperTypeFluteNorm;

    private double oneUps;
    private double twoUps;
    private double threeUps;
    private double fourUps;

    private String description;

    private double sellingPricePerBox;

    private double productionCostPerBox;

    private double fiveUps;
    private double sixUps;

    private double cuttingLengthOneUps;
    private double cuttingLengthTwoUps;

    private String piece;

    private String fluteType;
}