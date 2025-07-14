package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_order_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyOrderSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate summaryDate;

    @Column(nullable = false)
    private int totalOrdersShipped;

    @Column(nullable = false)
    private double totalWeightConsumed;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "summary_id")
    private List<OrderSummaryDetail> orderDetails = new ArrayList<>();

    @CreationTimestamp
    private ZonedDateTime createdAt;

    private double totalProfitOfDay;
    private double totalRevenueOfDay;

    private String totalProfitOfDayPercentage;

    private String totalRevenueOfDayPercentage;

    private double totalReelWastageOfDay;
    private String totalReelWastageOfDayPercentage;

}