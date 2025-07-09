package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reel_stock_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReelStockAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int deckle;

    private int gsm;

    private int totalWeight;

    private int threshold;

    @Column(length = 5, nullable = false)
    private String unit;

    private LocalDateTime alertTime;

    private boolean acknowledged = false;
}
