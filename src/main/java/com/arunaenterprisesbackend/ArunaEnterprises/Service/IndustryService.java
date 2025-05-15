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
    private CloudinaryService cloudinaryService;

    @Autowired
    private Cloudinary cloudinary;

    public String registerIndustry(IndustryDTO dto) {
        if (industryRepository.existsByIndustryName(dto.getIndustryName())) {
            return "Industry with the same name already exists.";
        }

        if (dto.getIndustryImage() == null || dto.getIndustryImage().isEmpty()) {
            return "Industry image is required.";
        }

        try {
            Map<String, Object> cloudinaryData = cloudinaryService.uploadBase64Image(dto.getIndustryImage());

            Industry industry = new Industry();
            industry.setIndustryName(dto.getIndustryName());
            industry.setSector(dto.getSector());
            industry.setCity(dto.getCity());
            industry.setState(dto.getState());
            industry.setAddress(dto.getAddress());

            byte[] imageBytes = Base64.getDecoder().decode(dto.getIndustryImage());
            industry.setIndustryImage(imageBytes);

            industry.setCloudinaryPublicId((String) cloudinaryData.get("public_id"));
            industry.setImageUrl((String) cloudinaryData.get("secure_url"));
            industry.setImageFormat((String) cloudinaryData.get("format"));

            Map uploadResult = (Map) cloudinaryData.get("upload_result");

            if (uploadResult.get("bytes") != null)
                industry.setImageSize((Integer) uploadResult.get("bytes"));

            if (uploadResult.get("width") != null)
                industry.setImageWidth((Integer) uploadResult.get("width"));

            if (uploadResult.get("height") != null)
                industry.setImageHeight((Integer) uploadResult.get("height"));

            industry.setUploadedAt(LocalDateTime.now());

            industryRepository.save(industry);

            return "Industry registered successfully.";

        } catch (IllegalArgumentException e) {
            return "Invalid image format. Must be a valid Base64 string.";
        } catch (IOException e) {
            return "Image upload failed: " + e.getMessage();
        } catch (Exception e) {
            return "Failed to register industry: " + e.getMessage();
        }
    }

    public void saveIndustry(Industry industry) {
        industryRepository.save(industry);
    }
}