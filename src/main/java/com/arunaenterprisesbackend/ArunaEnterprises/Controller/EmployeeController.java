package com.arunaenterprisesbackend.ArunaEnterprises.Controller;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.EmployeeRegister;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.EmployeeRegisterResponse;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.EmployeeResponseDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;


    @GetMapping("/employee/barcode-image/{id}")
    public ResponseEntity<byte[]> getBarcodeImage(@PathVariable Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(employee.getBarcodeImage());
    }

    @GetMapping("/employee/barcode/{barcodeId}")
    public ResponseEntity<byte[]> getBarcodeImageByBarcodeId(@PathVariable String barcodeId) {
        try {
            Employee employee = employeeRepository.findByBarcodeId(barcodeId);
            if (employee == null || employee.getBarcodeImage() == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(employee.getBarcodeImage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/employee/barcode-info/{barcodeId}")
    public ResponseEntity<Map<String, Object>> getBarcodeInfo(@PathVariable String barcodeId) {
        try {
            Employee employee = employeeRepository.findByBarcodeId(barcodeId);
            if (employee == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("barcodeId", employee.getBarcodeId());
            response.put("barcodeImage", Base64.getEncoder().encodeToString(employee.getBarcodeImage()));
            response.put("employeeName", employee.getName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @GetMapping("/employee/barcode-image/{barcodeId}")
//    public ResponseEntity<byte[]> getBarcodeImageByBarcodeId(@PathVariable String id) {
//        Employee employee = employeeRepository.findByBarcodeId(id);
//        if(employee == null){
//            return ResponseEntity.badRequest().body(null);
//        }
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_PNG)
//                .body(employee.getBarcodeImage());
//    }

    @PostMapping("/register-employee")
    public ResponseEntity<EmployeeRegisterResponse> registerEmployee(@RequestBody EmployeeRegister employeeRegister) {
        try {
            Employee employee = employeeService.registerEmployee(employeeRegister);
            EmployeeRegisterResponse response = new EmployeeRegisterResponse();
            response.setEmployeeId(employee.getId());
            response.setBarcodeId(employee.getBarcodeId());

            // Convert image to Base64
            String base64Image = Base64.getEncoder().encodeToString(employee.getBarcodeImage());
            response.setBarcodeImageBase64(base64Image);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/employee/register")
    public ResponseEntity<EmployeeRegisterResponse> EmployeeRegister(@RequestBody EmployeeRegister employeeRegister) {
        try {
            Employee employee = employeeService.registerEmployee(employeeRegister);
            EmployeeRegisterResponse response = new EmployeeRegisterResponse();
            response.setEmployeeId(employee.getId());
            response.setBarcodeId(employee.getBarcodeId());

            // Convert image to Base64
            String base64Image = Base64.getEncoder().encodeToString(employee.getBarcodeImage());
            response.setBarcodeImageBase64(base64Image);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/get-employees")
    public ResponseEntity<List<Employee>> getAllEmployees(){
        List<Employee> employees = employeeRepository.findAll();
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/employee/deactivate/{barcodeId}")
    public ResponseEntity<String> deactivateEmployee(@PathVariable String barcodeId) {
        try {
            Employee employee = employeeRepository.findByBarcodeId(barcodeId);
            if (employee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Employee with barcode ID " + barcodeId + " not found");
            }
            if (!employee.isActive()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Employee is already deactivated");
            }
            employee.setActive(false);
            employeeRepository.save(employee);
            return ResponseEntity.ok("Employee deactivated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deactivating employee: " + e.getMessage());
        }
    }

    @PostMapping("/employee/getEmployeeNameAndUnit")
    public ResponseEntity<?> getEmployeeDetails(String barcodeId){
        Employee employee = employeeRepository.findByBarcodeId(barcodeId);

        if(employee == null){
            return ResponseEntity.badRequest().body("Employee Not Found");
        }

        EmployeeResponseDTO employeeResponseDTO = new EmployeeResponseDTO();

        employeeResponseDTO.setEmployeeName(employee.getName());
        employeeResponseDTO.setUnit(employee.getUnit());

        return ResponseEntity.ok(employeeResponseDTO);
    }
}
