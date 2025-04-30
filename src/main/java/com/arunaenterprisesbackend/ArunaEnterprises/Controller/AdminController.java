package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.Config.SecurityConfig;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.AttendanceResponseDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.EmployeeRegister;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.LoginResponse;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Admin;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Attendance;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AdminRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.AttendanceRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.AdminService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.EmployeeService;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private AttendanceRepository attendanceRepository;

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

    @PostMapping("/register-employee")
    public ResponseEntity<String> registerEmployee(@RequestBody EmployeeRegister employeeRegister) {
        try {
            String response = employeeService.registerEmployee(employeeRegister);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to register employee: " + e.getMessage());
        }
    }

        @GetMapping("/employee/barcode-image/{id}")
    public ResponseEntity<byte[]> getBarcodeImage(@PathVariable Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(employee.getBarcodeImage());
    }

    @GetMapping("/get-employees")
    public ResponseEntity<List<Employee>> getAllEmployees(){
        List<Employee> employees = employeeRepository.findAll();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/get-admins")
    public ResponseEntity<List<Admin>> getAllAdmins(){
        List<Admin> admins = adminRepository.findAll();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/attendance-list")
    public ResponseEntity<List<AttendanceResponseDTO>> getAttendanceByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Employee> employees = employeeRepository.findAll();

        List<Attendance> attendanceRecords = attendanceRepository.findByDate(date);

        Map<String, Attendance> attendanceMap = attendanceRecords.stream()
                .collect(Collectors.toMap(Attendance::getBarcodeId, Function.identity()));

        List<AttendanceResponseDTO> response = employees.stream()
                .map(employee -> {
                    Attendance attendance = attendanceMap.get(employee.getBarcodeId());

                    if (attendance != null) {
                        return new AttendanceResponseDTO(
                                employee.getName(),
                                employee.getBarcodeId(),
                                attendance.getDate(),
                                attendance.getCheckInTime(),
                                attendance.getCheckOutTime(),
                                attendance.getStatus().toString()
                        );
                    } else {
                        return new AttendanceResponseDTO(
                                employee.getName(),
                                employee.getBarcodeId(),
                                date,
                                null,
                                null,
                                "ABSENT"
                        );
                    }
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
