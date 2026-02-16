package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    boolean existsByEmail(String email);
    boolean existsByName(String name);
    Employee findByBarcodeId(String barcodeId);
    List<Employee> findAll();
    List<Employee> findByIsActive(boolean isActive);

    @Query("SELECT e FROM Employee e WHERE e.isActive = true")
    List<Employee> findActiveEmployees();

    Optional<Employee> findByPinCode(int pinCode);
}
