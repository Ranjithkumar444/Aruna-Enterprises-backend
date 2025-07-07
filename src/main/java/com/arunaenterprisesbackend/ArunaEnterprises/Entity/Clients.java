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

    private String productType;
    private String size;
    private String ply;
    private double deckle;
    private double cuttingLength;
    private int topGsm;
    private int bottomGsm;
    private int linerGsm;
    private int fluteGsm;

    private String madeUpOf;
    private String paperTypeTop;
    private String paperTypeBottom;
    private double oneUps;
    private double twoUps;
    private double threeUps;
    private double fourUps;

    private String description;

    private double sellingPricePerBox;

    private double productionCostPerBox;
}
