package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.Config.SecurityConfig;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.BoxDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.CreateAdminRequestDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.LoginResponse;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.AdminService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.EmployeeService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.IndustryService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecurityConfig securityConfig;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private IndustryService industryService;

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private IndustryRepository industryRepository;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Admin admin) {
        try {
            String email = adminService.verify(admin); // Authenticates the user
            Admin existingUser = adminRepository.findByEmail(email); // Retrieve admin to get their role

            if (existingUser == null) {
                // This case should ideally be handled by adminService.verify
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String token = jwtService.generateToken(email); // Token now includes the role
            // LoginResponse now directly gets the role from the existingUser
            LoginResponse response = new LoginResponse(token, existingUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Catches UsernameNotFoundException, BadCredentialsException from AdminService.verify
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPER_ADMIN','ADMIN')") // Only a SUPER_ADMIN can call this endpoint
    public ResponseEntity<?> createAdmin(@RequestBody CreateAdminRequestDTO createAdminRequest) {
        if (adminRepository.existsByEmail(createAdminRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Admin with this email already exists"));
        }

        Admin newAdmin = new Admin();
        newAdmin.setUserName(createAdminRequest.getUserName());
        newAdmin.setFirstName(createAdminRequest.getFirstName());
        newAdmin.setLastName(createAdminRequest.getLastName());
        newAdmin.setEmail(createAdminRequest.getEmail());
        newAdmin.setPhoneNumber(createAdminRequest.getPhoneNumber());
        newAdmin.setGender(createAdminRequest.getGender());
        newAdmin.setPassword(passwordEncoder.encode(createAdminRequest.getPassword()));

        if (createAdminRequest.getRole() == null ||
                (createAdminRequest.getRole() != AdminRole.ROLE_ADMIN && createAdminRequest.getRole() != AdminRole.ROLE_SUPER_ADMIN)) {
            newAdmin.setRole(AdminRole.ROLE_ADMIN);
        } else {
            newAdmin.setRole(createAdminRequest.getRole());
        }

        Admin savedAdmin = adminRepository.save(newAdmin);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')") // Accessible by both roles
    public String adminDashboard() {
        return "Hello this is admin DashBoard";
    }

    @GetMapping("/get-admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')") // Only SUPER_ADMIN can view all admins
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/contact/contactDetails")
    public ResponseEntity<List<ContactMessage>> getContactRequest() {
        try {
            // Note: This needs to be within an authenticated endpoint, check SecurityConfig.
            // Also, this line `LocalDateTime cutoff = LocalDateTime.now().minusHours(168);` seems to be outside the method in your snippet
            // It should be inside like this:
            LocalDateTime cutoff = LocalDateTime.now().minusHours(168);
            List<ContactMessage> recentMessages = contactRepository.findMessagesFromLast48Hours(cutoff);
            return ResponseEntity.ok(recentMessages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/contact/updateReplyStatus/{id}")
    public ResponseEntity<?> updateReplyStatus(
            @PathVariable Long id,
            @RequestBody UpdateReplyStatusRequest request) {
        // Need to ensure this is secured as well in SecurityConfig if not already
        ContactMessage updatedContact = adminService.updateReplyStatus(id, request.isReplyStatus());
        return ResponseEntity.ok(updatedContact);
    }

    @PostMapping("/box/create-box")
    public ResponseEntity<String> createBox(@RequestBody BoxDTO boxDTO) {
        try {
            // Need to ensure this is secured as well in SecurityConfig if not already
            Box box = new Box();
            box.setBox(boxDTO.getBox());
            box.setBoxType(boxDTO.getBoxType());
            box.setBoxUrl(boxDTO.getBoxUrl());
            box.setBoxDescription(boxDTO.getBoxDescription());
            boxRepository.save(box);
            return ResponseEntity.ok("The box details registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to register box details");
        }
    }
}