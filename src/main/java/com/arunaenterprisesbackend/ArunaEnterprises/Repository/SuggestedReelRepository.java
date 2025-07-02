package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.SuggestedReel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuggestedReelRepository extends JpaRepository<SuggestedReel, Long> {

    Optional<SuggestedReel> findByClientNormalizerAndSize(String clientNormalizer,String size);
}
