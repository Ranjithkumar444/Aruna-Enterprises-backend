package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndustryDTO {
    private String industryName;
    private String city;
    private String sector;
    private String state;
    private String address;
}
