package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.DTO.ReelRegistrationResponseDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Reel;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.ReelRepository;
import com.arunaenterprisesbackend.ArunaEnterprises.Utility.BarcodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.ReelStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


@Service
public class ReelService {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    @Autowired
    private ReelRepository reelRepository;

    public ReelRegistrationResponseDTO registerReel(ReelDTO reeldata) throws Exception {
        Reel reel = new Reel();
        reel.setStatus(ReelStatus.NOT_IN_USE);
        reel.setDeckle(reeldata.getDeckle());
        reel.setGsm(reeldata.getGsm());
        reel.setBurstFactor(reeldata.getBurstFactor());
        reel.setCreatedBy(reeldata.getCreatedBy());
        reel.setInitialWeight(reeldata.getInitialWeight());
        reel.setCurrentWeight(reeldata.getInitialWeight());
        reel.setReelNo(reeldata.getReelNo());
        reel.setPreviousWeight(reeldata.getInitialWeight());
        reel.setSupplierName(reeldata.getSupplierName());
        reel.setPaperTypeNormalized(reeldata.getPaperType().toLowerCase().replaceAll("[^a-z0-9]", ""));
        reel.setUnit(reeldata.getUnit());
        reel.setPaperType(reeldata.getPaperType());

        LocalDate createdAt = LocalDate.now(IST_ZONE);
        reel.setCreatedAt(createdAt);

        long countToday = reelRepository.countByCreatedAt(createdAt);

        String dateString = createdAt.toString().replaceAll("-", ""); // e.g. 20250707
        String barcodeId = "REEL-" + (countToday + 1) + dateString;
        reel.setBarcodeId(barcodeId);

        byte[] barcodeImage = BarcodeGenerator.generateBarcodeImage(barcodeId);
        reel.setBarcodeImage(barcodeImage);

        reelRepository.save(reel);

        return new ReelRegistrationResponseDTO(barcodeId);
    }

}