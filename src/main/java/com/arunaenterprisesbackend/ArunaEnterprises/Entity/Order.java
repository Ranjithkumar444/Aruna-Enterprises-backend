package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String client;

    private String productType;

    private String typeOfProduct;

    private int quantity;

    private String size;

    private String materialGrade;

    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    private ZonedDateTime orderCreatedDate;

    private ZonedDateTime expectedCompletionDate;

    private String createdBy;

    @Column(name = "completed_at")
    private ZonedDateTime completedAt;

    @Column(name = "shipped_at")
    private ZonedDateTime shippedAt;

    private String unit;

    private String transportNumber;

    @Column(name = "normalized_client")
    private String normalizedClient;
}