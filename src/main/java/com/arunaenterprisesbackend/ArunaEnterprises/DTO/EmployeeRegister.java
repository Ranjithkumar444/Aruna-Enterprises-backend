package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;
import org.springframework.stereotype.Service;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeRegister {
    private String name;
    private String email;
    private String unit;
    private String gender;
    private String phoneNumber;
    private String bloodGroup;
    private String dob;
}
