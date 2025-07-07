package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestedLayerReelsDTO {
    private List<SuggestedReelDTO> topReels;
    private List<SuggestedReelDTO> bottomReels;
    private List<SuggestedReelDTO> fluteReels;
}