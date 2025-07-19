package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Admin;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.AdminRole;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String message;
    private Admin admin;

    public LoginResponse(Object o, String s) {
    }
}
