package com.arunaenterprisesbackend.ArunaEnterprises.Repository;


import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReelRepository extends JpaRepository<Reel, Long> {

    Reel findByBarcodeId(String barcodeId);

    Reel findByBarcodeIdOrReelNo(String barcodeId, Long reelNo);

    Reel findByReelNo(Long reelNo);

    List<Reel> findBySupplierName(String supplierName);

    List<Reel> findByStatusIn(List<ReelStatus> statuses);

    @Query("SELECT r FROM Reel r WHERE LOWER(r.barcodeId) = LOWER(:barcodeId)")
    Reel findByBarcodeIdIgnoreCase(@Param("barcodeId") String barcodeId);

    List<Reel> findByBarcodeIdContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    @Query("SELECT r FROM Reel r WHERE r.deckle >= :minDeckle AND r.deckle <= :maxDeckle AND r.status IN :statuses")
    List<Reel> findAvailableByDeckleRange(
            @Param("minDeckle") int minDeckle, // Changed to int
            @Param("maxDeckle") int maxDeckle, // Changed to int
            @Param("statuses") List<ReelStatus> statuses
    );

    @Query("""
      SELECT r FROM Reel r
      WHERE r.status IN :statuses
        AND r.deckle BETWEEN :minDeckle AND :maxDeckle
        AND r.currentWeight > 0
      """)
    List<Reel> findAvailableByDeckleRange(
            @Param("minDeckle") double minDeckle,
            @Param("maxDeckle") double maxDeckle,
            @Param("statuses") List<ReelStatus> statuses
    );
    long countByCreatedAt(LocalDate createdAt);


    @Query("SELECT r.deckle, r.gsm, r.unit, SUM(r.currentWeight) " +
            "FROM Reel r GROUP BY r.deckle, r.gsm, r.unit")
    List<Object[]> getGroupedWeightByDeckleGsmAndUnit();

    @Query("SELECT r FROM Reel r WHERE r.gsm = ?1 AND r.status IN ('NOT_IN_USE', 'PARTIALLY_USED_AVAILABLE')")
    List<Reel> findByGsmAndAvailableStatus(int gsm);

    @Query("SELECT r FROM Reel r WHERE r.reelSet = ?1 ORDER BY r.reelNo")
    List<Reel> findByReelSet(String reelSet);

    @Query("SELECT r FROM Reel r WHERE " +
            "r.deckle BETWEEN :minDeckle AND :maxDeckle AND " +
            "r.paperTypeNormalized = :paperTypeNorm AND " +
            "r.status IN :statuses")
    List<Reel> findAvailableByDeckleRangeAndPaperTypeNorm(
            @Param("minDeckle") double minDeckle,
            @Param("maxDeckle") double maxDeckle,
            @Param("paperTypeNorm") String paperTypeNorm,
            @Param("statuses") List<ReelStatus> statuses);

}