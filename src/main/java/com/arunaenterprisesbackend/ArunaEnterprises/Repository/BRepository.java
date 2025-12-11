package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.B;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BRepository extends JpaRepository<B, Long> {
    B findByProduct(String product);
}