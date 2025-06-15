package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.IndustryDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Industry;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.IndustryRepository;
import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class IndustryService {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    @Autowired
    private IndustryRepository industryRepository;

    @Autowired
    private Cloudinary cloudinary;

    public String registerIndustry(IndustryDTO dto) {
        if (industryRepository.existsByIndustryName(dto.getIndustryName())) {
            return "Industry with the same name already exists.";
        }

        try {

            Industry industry = new Industry();
            industry.setIndustryName(dto.getIndustryName());
            industry.setSector(dto.getSector());
            industry.setCity(dto.getCity());
            industry.setState(dto.getState());
            industry.setAddress(dto.getAddress());
            industry.setUploadedAt(ZonedDateTime.now(IST_ZONE).toLocalDateTime());

            industryRepository.save(industry);

            return "Industry registered successfully.";

        } catch (IllegalArgumentException e) {
            return "Invalid image format. Must be a valid Base64 string.";
        } catch (Exception e) {
            return "Failed to register industry: " + e.getMessage();
        }
    }

    public void saveIndustry(Industry industry) {
        industryRepository.save(industry);
    }
}