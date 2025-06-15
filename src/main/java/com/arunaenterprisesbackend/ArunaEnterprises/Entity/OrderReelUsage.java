package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_reel_usage")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderReelUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "reel_id")
    private Reel reel;

    private double weightConsumed;
    private LocalDateTime courgationIn;
    private LocalDateTime courgationOut;
    private String recordedBy;

    private String usageType;

    private int howManyBox;
}