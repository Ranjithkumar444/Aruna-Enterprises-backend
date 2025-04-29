package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "reel")
@Setter
@Getter
public class Reel {
    @Id
    @Column(name = "barcode_id", length = 50, nullable = false)
    private String barcodeId;

    @Column(length = 50)
    private String size;

    private Double weight;

    @Column(length = 100)
    private String quality;

    @Column(name = "supplier_name", length = 100)
    private String supplierName;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ReelStatus status;

    @Column(name = "barcode_image", columnDefinition = "TEXT")
    private String barcodeImage;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    public Reel() {
        this.createdAt = LocalDate.now();
    }
}
