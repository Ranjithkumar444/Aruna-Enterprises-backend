package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Admin;
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
            Admin admin = new Admin();
            admin.setUserName("RanjithKumar");
            admin.setFirstName("Ranjith");
            admin.setLastName("Kumar");
            admin.setEmail("ranjith.v.kumar444@gmail.com");
            admin.setPassword(passwordEncoder.encode("ranjith1234"));
            admin.setPhoneNumber("9876543210");
            admin.setGender("Male");
            adminRepository.save(admin);
            System.out.println("Admin inserted successfully.");
        }
    }
}

