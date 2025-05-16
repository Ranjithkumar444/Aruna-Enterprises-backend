package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reel_usage_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReelUsageHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String barcodeId;

    private double usedWeight;

    private LocalDateTime usedAt;

    private String usedBy;

    private String reelSet;

    private String boxDetails;
}
