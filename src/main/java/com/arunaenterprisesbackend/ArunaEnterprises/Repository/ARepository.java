package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.A;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ARepository extends JpaRepository<A, Long> {
    A findByProduct(String product);
}
