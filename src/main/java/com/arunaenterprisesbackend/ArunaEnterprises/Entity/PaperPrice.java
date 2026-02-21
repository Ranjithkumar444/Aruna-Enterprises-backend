package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "paper_price",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"paperTypeNormalized", "gsm", "bf"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaperPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paperType;
    private String paperTypeNormalized;

    private int gsm;
    private int bf;

    private double pricePerKg;
}