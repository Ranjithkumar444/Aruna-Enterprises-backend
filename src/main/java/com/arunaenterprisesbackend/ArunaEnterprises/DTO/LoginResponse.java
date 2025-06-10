package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Admin;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.AdminRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Admin admin;
    private AdminRole role;

    public LoginResponse(String token, Admin admin) {
        this.token = token;
        this.admin = admin;
        this.role = admin.getRole();
    }
}
