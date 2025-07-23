package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reels")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Reel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "barcode_id", unique = true, length = 100)
    private String barcodeId;

    private Long reelNo;

    private int gsm;

    @Column(name = "burst_factor")
    private int burstFactor;

    private int deckle;

    @Column(name = "initial_weight")
    private int initialWeight;

    @Column(name = "previous_weight")
    private Integer previousWeight = 0;;

    @Column(name = "current_weight")
    private int currentWeight;

    @Column(length = 5)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ReelStatus status;

    @Column(name = "paper_type", length = 100)
    private String paperType;

    @Column(name = "supplier_name", length = 100)
    private String supplierName;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Lob
    @Column(name = "barcode_image")
    private byte[] barcodeImage;

    private String reelSet;

    private String paperTypeNormalized;
//    private String twoSheetOneBoxReelSet;
}
