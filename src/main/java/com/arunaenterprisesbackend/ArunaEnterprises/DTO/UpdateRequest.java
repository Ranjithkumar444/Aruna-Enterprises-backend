package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequest {
    @NotBlank(message = "product is required")
    private String product;   // e.g. "PastingGumBag"

    @NotNull(message = "count is required")
    @Min(value = 0, message = "count must be >= 0")
    private Integer count;    // new count value
}