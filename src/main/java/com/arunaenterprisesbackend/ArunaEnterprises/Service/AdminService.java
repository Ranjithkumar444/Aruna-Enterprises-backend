package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Admin;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ContactMessage;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AdminRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private ContactRepository contactRepository;

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

    public ContactMessage updateReplyStatus(Long id, boolean replyStatus) {
        Optional<ContactMessage> contactOptional = contactRepository.findById(id);
        if (contactOptional.isPresent()) {
            ContactMessage contact = contactOptional.get();
            contact.setReplyStatus(replyStatus);
            return contactRepository.save(contact);
        } else {
            throw new RuntimeException("Contact not found with id: " + id);
        }
    }
}
