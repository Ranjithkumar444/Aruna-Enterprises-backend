package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    Admin findByEmail(String email);
    boolean existsByEmail(String email);
    List<Admin> findByActiveTrue();
}
