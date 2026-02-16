package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.EmployeeRegister;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.EmployeeRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Utility.BarcodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class EmployeeService {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee registerEmployee(EmployeeRegister employeeRegister) throws Exception {
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        employee.setDob(LocalDate.parse(employeeRegister.getDob(), formatter));

        employee.setJoinedAt(ZonedDateTime.now(IST_ZONE));

        String barcodeID = generateBarcodeId();
        employee.setBarcodeId(barcodeID);

        String numberPart = barcodeID.split("-")[1];
        int employeePinCode = Integer.parseInt(numberPart);

        employee.setPinCode(employeePinCode);

        byte[] barcodeImage = BarcodeGenerator.generateBarcodeImage(barcodeID);
        employee.setBarcodeImage(barcodeImage);

        employeeRepository.save(employee);

        return employee;
    }

    public String generateBarcodeId() {
        Random random = new Random();
        String barcodeId;
        Employee existingEmployee;

        do {
            int randomNumber = 100 + random.nextInt(1900);
            barcodeId = "EMP-" + randomNumber;
            existingEmployee = employeeRepository.findByBarcodeId(barcodeId);
        } while (existingEmployee != null);

        return barcodeId;
    }

}
