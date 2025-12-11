package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.C;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CRepository extends JpaRepository<C, Long> {
    C findByProduct(String product);
}