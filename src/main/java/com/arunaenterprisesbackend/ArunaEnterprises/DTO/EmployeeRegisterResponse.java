package com.arunaenterprisesbackend.ArunaEnterprises.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRegisterResponse {
    private Long employeeId;
    private String barcodeId;
    private String barcodeImageBase64;
}
