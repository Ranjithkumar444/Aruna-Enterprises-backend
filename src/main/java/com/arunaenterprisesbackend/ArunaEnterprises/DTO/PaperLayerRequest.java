package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaperLayerRequest {

    private String paperType;
    private String paperTypeNormalized;
    private int gsm;
    private int bf;

}