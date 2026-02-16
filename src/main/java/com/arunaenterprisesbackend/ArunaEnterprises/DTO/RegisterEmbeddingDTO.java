package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RegisterEmbeddingDTO {

    private List<Float> embedding;
    private int pinCode;
}
