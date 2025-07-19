package com.arunaenterprisesbackend.ArunaEnterprises.Repository;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.SuggestedReel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuggestedReelRepository extends JpaRepository<SuggestedReel, Long> {

    Optional<SuggestedReel> findByClientNormalizerAndSize(String clientNormalizer,String size);

    @Query("SELECT s FROM SuggestedReel s WHERE s.product = ?1 AND s.size = ?2")
    List<SuggestedReel> findByProductAndSize(String product, String size);

    @Query("SELECT DISTINCT s.client FROM SuggestedReel s")
    List<String> findAllDistinctClients();

    Optional<SuggestedReel> findByClientNormalizerAndSizeAndProduct(String normalizedClient, String size, String productName);

}
