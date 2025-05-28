package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BoxDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String boxSector;
    private String heading;
    private String capacity;
    private String noOfPly;
    private String shape;
    private String printingType;
    private String application;
    private String paperGrade;
    private String gsm;
    private String color;
    private String property;
    private String url;
}
