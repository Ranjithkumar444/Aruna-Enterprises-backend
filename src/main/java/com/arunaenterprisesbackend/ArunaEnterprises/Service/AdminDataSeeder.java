package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Admin;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.AdminRole;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminDataSeeder implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (adminRepository.findByEmail("ranjith.v.kumar444@gmail.com") == null) {
            Admin superAdmin = new Admin();
            superAdmin.setUserName("ranjithkumar"); // Changed for clarity
            superAdmin.setFirstName("Ranjith");
            superAdmin.setLastName("Kumar");
            superAdmin.setEmail("ranjith.v.kumar444@gmail.com");
            superAdmin.setPassword(passwordEncoder.encode("ranjith1234"));
            superAdmin.setPhoneNumber("9876543210");
            superAdmin.setGender("Male");
            superAdmin.setRole(AdminRole.ROLE_SUPER_ADMIN);
            adminRepository.save(superAdmin);
            System.out.println("Initial Super Admin created successfully.");
        }
    }
}
