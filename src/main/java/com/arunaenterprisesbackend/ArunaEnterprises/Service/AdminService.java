package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Admin;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public AdminService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    public String verify(Admin admin) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(admin.getEmail(), admin.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return admin.getEmail();
    }
}
