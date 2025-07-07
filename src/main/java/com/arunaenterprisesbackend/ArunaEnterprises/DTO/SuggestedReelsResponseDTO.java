package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class SuggestedReelsResponseDTO {
    private List<SuggestedReelDTO> topGsmReels;
    private List<SuggestedReelDTO> bottomGsmReels;
    private List<SuggestedReelDTO> fluteGsmReels;
    private boolean fluteRequired;
    private String message;
    private double topExpectedWeight;
    private double linerExpectedWeight;
    private double fluteExpectedWeight;

}