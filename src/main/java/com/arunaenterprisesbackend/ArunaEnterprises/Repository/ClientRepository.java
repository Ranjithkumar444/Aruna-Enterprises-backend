package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Clients;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Clients,Long> {
    Optional<Clients> findByClientNormalizerAndSize(String clientNormalizer, String size);
    Optional<Clients> findByClientNormalizerAndSizeAndProduct(String clientNormalizer, String size,String product);
    Optional<Clients> findByClientNormalizer(String clientNormalizer);

}
