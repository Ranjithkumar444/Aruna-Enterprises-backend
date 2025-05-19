package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;


@Entity
@Table(name = "employee")
@Setter
@Getter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String barcodeId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String unit;

    private String gender;

    @Column(nullable = false)
    private String phoneNumber;

    private LocalDate joinedAt;

    private String bloodGroup;

    private LocalDate dob;

    @Lob
    @Column(name = "barcode_image", columnDefinition = "LONGBLOB")
    private byte[] barcodeImage;

    @Column(name = "is_active")
    private boolean isActive = true;
}
