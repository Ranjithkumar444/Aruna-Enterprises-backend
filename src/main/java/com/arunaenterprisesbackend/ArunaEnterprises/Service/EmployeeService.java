package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.EmployeeRegister;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Utility.BarcodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public String registerEmployee(EmployeeRegister employeeRegister) throws Exception {
        Employee employee = new Employee();

        if (employeeRepository.existsByEmail(employeeRegister.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (employeeRepository.existsByName(employeeRegister.getName())) {
            throw new RuntimeException("Name already registered");
        }


        Employee employee = new Employee();

        employee.setName(employeeRegister.getName());
        employee.setEmail(employeeRegister.getEmail());
        employee.setUnit(employeeRegister.getUnit());
        employee.setGender(employeeRegister.getGender());
        employee.setPhoneNumber(employeeRegister.getPhoneNumber());
        employee.setBloodGroup(employeeRegister.getBloodGroup());

        employee.setDob(LocalDate.parse(employeeRegister.getDob())); // expects yyyy-MM-dd

        String barcodeId = UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        employee.setBarcodeId(barcodeId);
        employee.setBarcodeImage(BarcodeGenerator.generateBarcodeImage(barcodeId));

        // Set joinedAt
        employee.setJoinedAt(LocalDate.now());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        employee.setDob(LocalDate.parse(employeeRegister.getDob(), formatter));


        // Set current date
        employee.setJoinedAt(LocalDate.now());

        // Generate barcode ID
        String barcodeId = UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        employee.setBarcodeId(barcodeId);

        // Generate barcode image
        byte[] barcodeImage = BarcodeGenerator.generateBarcodeImage(barcodeId);
        employee.setBarcodeImage(barcodeImage);

        // Save to DB
        employeeRepository.save(employee);

        return "Employee registered with Barcode ID: " + barcodeId;
    }
}
