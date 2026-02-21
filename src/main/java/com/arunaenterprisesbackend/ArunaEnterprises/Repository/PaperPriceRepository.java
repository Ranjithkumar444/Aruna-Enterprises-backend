package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.PaperPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaperPriceRepository extends JpaRepository<PaperPrice, Long> {

    Optional<PaperPrice> findByPaperTypeNormalizedAndGsmAndBf(
            String paperTypeNormalized, int gsm, int bf
    );

    List<PaperPrice> findAll();
}
