package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.D;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DRepository extends JpaRepository<D, Long> {
    D findByProduct(String product);
}