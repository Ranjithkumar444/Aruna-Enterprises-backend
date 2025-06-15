package com.arunaenterprisesbackend.ArunaEnterprises.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReelUsageResponseDTO {
    private String client;
    private String productType;
    private Integer quantity;
    private String size;
    private String unit;
    private Integer howManyBox;
    private String reelSet;
    private Double weightConsumed;
    private String usageType;
    private String paperType;
    private Integer gsm;
    private Integer burstFactor;
    private Integer deckle;
    private LocalDateTime courgationIn;
    private LocalDateTime courgationOut;
}
