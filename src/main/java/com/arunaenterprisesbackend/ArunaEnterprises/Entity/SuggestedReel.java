package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;

@Entity
@Table(name = "suggested_reel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestedReel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String client, clientNormalizer, productNormalizer ,product, size, ply;
    private double deckle, cuttingLength;
    private int topGsm, bottomGsm, linerGsm, fluteGsm;
    private String madeUpOf, paperTypeTop, paperTypeBottom;
    private double oneUps, twoUps, threeUps, fourUps;
    private String description;
    private double sellingPricePerBox;
    private double productionCostPerBox;
    private String paperTypeFlute;


    private String paperTypeTopNorm;
    private String paperTypeBottomNorm;
    private String paperTypeFluteNorm;


    private double fiveUps;
    private double sixUps;
    private double cuttingLengthOneUps;
    private double cuttingLengthTwoUps;
    @AssertTrue(message = "Flute GSM must be provided when different from liner")
    public boolean isFluteValid() {
        return (linerGsm == fluteGsm) || fluteGsm > 0;
    }
}
