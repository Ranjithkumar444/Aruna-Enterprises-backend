package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.DTO.IndustryDTO;
import com.arunaenterprisesbackend.ArunaEnterprises.Entity.Industry;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.IndustryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Base64;

@Service
public class IndustryService {

    @Autowired
    private IndustryRepository industryRepository;

    public String registerIndustry(IndustryDTO dto) {
        // Check if industry already exists
        if (industryRepository.existsByIndustryName(dto.getIndustryName())) {
            return "Industry with the same name already exists.";
        }

        // Validate Base64 image (if provided)
        if (dto.getIndustryImage() == null || dto.getIndustryImage().isEmpty()) {
            return "Industry image is required.";
        }

        try {
            Industry industry = new Industry();
            industry.setIndustryName(dto.getIndustryName());

            // Decode Base64 image
            byte[] imageBytes = Base64.getDecoder().decode(dto.getIndustryImage());
            industry.setIndustryImage(imageBytes);

            // Set other fields
            industry.setCity(dto.getCity());
            industry.setState(dto.getState());
            industry.setAddress(dto.getAddress());
            industry.setSector(dto.getSector());

            // Save the industry
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