package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_summary_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private double topWeightConsumed;
    private double linerWeightConsumed;
    private double fluteWeightConsumed;
    private double totalWeightConsumed;

    private double profit;

    private String profitPercentage;

    private double revenue;

    private String revenuePercentage;

    private double totalReelWastage;

    private String totalReelWastagePercentage;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "detail_id")
    private List<ReelUsageSummary> reelUsages = new ArrayList<>();
}
