package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employee")
@Setter
@Getter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String barcodeId;

    @Column(nullable = false,unique = true)
    private String name;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String unit;

    private String gender;

    @Column(nullable = false)
    private String phoneNumber;

    private LocalDate joinedAt;

    @Lob
    @Column(name = "barcode_image", columnDefinition = "LONGBLOB")
    private byte[] barcodeImage;

    @PrePersist
    public void prePersist() {
        this.joinedAt = LocalDate.now();
        this.barcodeId = generateRandomBarcodeId();
    }

    private String generateRandomBarcodeId() {
        return UUID.randomUUID().toString().substring(0, 10).toUpperCase(); // 10-char unique barcode ID
    }

}
