package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "industry")
public class Industry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "industry_name",nullable = false , unique = true)
    private String industryName;

    @Column(nullable = false)
    private String sector;

    private String city;

    private String state;

    private String address;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    private String url;
}
