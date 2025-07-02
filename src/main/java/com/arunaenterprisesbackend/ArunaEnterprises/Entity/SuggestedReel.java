package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;

@Entity
@Table(name = "suggested_reel")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestedReel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String client;
    private String clientNormalizer;
    private String product;
    private String size;
    private String ply;
    private int deckle;
    private double cuttingLength;
    private int topGsm;
    private int bottomGsm;
    private int linerGsm;
    private int fluteGsm;
    private String madeUpOf;
    private String paperTypeTop;
    private String paperTypeBottom;

    @AssertTrue(message = "Flute GSM must be provided when different from liner")
    public boolean isFluteValid() {
        return (linerGsm == fluteGsm) || (fluteGsm > 0);
    }
}

