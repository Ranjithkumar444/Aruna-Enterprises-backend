package com.arunaenterprisesbackend.ArunaEnterprises.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reel_stock_thresholds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReelStockThreshold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int deckle;

    private int gsm;

    @Column(name = "min_weight_threshold")
    private int minWeightThreshold;

    @Column(length = 5, nullable = false)
    private String unit;
}
