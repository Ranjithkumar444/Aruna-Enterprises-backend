package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.AdminRole;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdminRequestDTO {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String gender;
    private AdminRole role;
}