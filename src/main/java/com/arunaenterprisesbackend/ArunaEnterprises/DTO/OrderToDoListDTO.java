package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.OrderStatus;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ProductionDetail;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderToDoListDTO {

    private Long id;

    @Column(nullable = false)
    private String client;

    private String productType; //Ply 3-ply

    private String typeOfProduct; // Corrugated or punching

    private String productName; // Own product name like

    private int quantity;

    private String size;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "production_detail_id", referencedColumnName = "id")
    @JsonManagedReference
    private ProductionDetail productionDetail;

    private int deckle;

    private int topGsm;

    private int fluteGsm;

    private int linerGsm;

    private int burstFactor;
}
