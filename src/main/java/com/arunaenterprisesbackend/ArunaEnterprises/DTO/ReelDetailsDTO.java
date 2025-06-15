package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReelDetailsDTO {
    private Long reelNo;
    private int gsm;
    private int deckle;
    private int burstFactor;
    private int currentWeight;
    private String supplierName;
}
