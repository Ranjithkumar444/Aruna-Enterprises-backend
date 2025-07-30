package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class SuggestedReelsResponseDTO {
    private List<SuggestedReelDTO> topGsmReels;
    private List<SuggestedReelDTO> bottomGsmReels;
    private List<SuggestedReelDTO> fluteGsmReels;
    private boolean fluteRequired;
    private String message;
    private long orderid;
    private int minDeckle;
    private int maxDeckle;
    private int minCuttingLength;
    private int maxCuttingLength;
}