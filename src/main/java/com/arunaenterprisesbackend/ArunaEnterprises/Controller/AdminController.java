package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.Config.SecurityConfig;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.LoginResponse;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Admin;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ContactMessage;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.*;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.AdminService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.EmployeeService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.IndustryService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "*")
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
    private IndustryRepository industryRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Admin admin) {
        try {
            String email = adminService.verify(admin);
            String token = jwtService.generateToken(email);
            Admin existingUser = adminRepository.findByEmail(email);

            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            LoginResponse response = new LoginResponse(token, existingUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("/create")
    public ResponseEntity<?> createAdmin(@RequestBody Admin admin) {
        if (adminRepository.existsByEmail(admin.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Admin with this email already exists"));
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        Admin savedAdmin = adminRepository.save(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
    }

    @GetMapping("/dashboard")
    public String adminDashboard(){
        return "Hello this is admin DashBoard";
    }

    @GetMapping("/get-admins")
    public ResponseEntity<List<Admin>> getAllAdmins(){
        List<Admin> admins = adminRepository.findAll();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/contact/contactDetails")
    public ResponseEntity<List<ContactMessage>> getContactRequest(){
        try{
            LocalDateTime cutoff = LocalDateTime.now().minusHours(48);
            List<ContactMessage> recentMessages = contactRepository.findMessagesFromLast48Hours(cutoff);
            return ResponseEntity.ok(recentMessages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
