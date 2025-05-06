package com.arunaenterprisesbackend.ArunaEnterprises.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContactDTO {
    String name;
    String email;
    String phone;
    String message;
}
