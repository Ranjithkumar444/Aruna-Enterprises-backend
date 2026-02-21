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
    private String size; //403 X 459 X 233
    private String ply; //3 - ply layers
    private double deckle; //length
    private double cuttingLength;
    private int topGsm;
    private int linerGsm;
    private int bottomGsm;
    private int fluteGsm;

    private String madeUpOf; //2-piece , 1 ups
    private String paperTypeTop; // Natural , duplex
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

    private double fiveUps;
    private double sixUps;

    private double cuttingLengthOneUps;
    private double cuttingLengthTwoUps;

    private String piece;

    private String fluteType;

    private double conversionCost;
}