package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.IndustryDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Industry;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.IndustryRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
public class IndustryService {

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
            industry.setUploadedAt(LocalDateTime.now());

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