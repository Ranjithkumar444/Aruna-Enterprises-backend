package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "machine")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String machineCode;
    private String machineName;
    private String unit;
    private double maxCuttingLength;
    private double minCuttingLength;
    private double maxDeckle;
    private double minDeckle;
    private double noOfSheetsPerHour;
    private double noOfBoxPerHour;
}
