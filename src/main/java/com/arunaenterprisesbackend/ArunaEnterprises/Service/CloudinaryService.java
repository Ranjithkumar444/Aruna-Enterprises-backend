package com.arunaenterprisesbackend.ArunaEnterprises.Service;

import com.arunaenterprisesbackend.ArunaEnterprises.Entity.CloudinaryImage;
import com.arunaenterprisesbackend.ArunaEnterprises.Repository.CloudinaryImageRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private CloudinaryImageRepository cloudinaryImageRepository;

    public Map<String,Object> uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

        String publicId = (String) uploadResult.get("public_id");
        String url = (String) uploadResult.get("url");
        String secureUrl = (String) uploadResult.get("secure_url");
        String format = (String) uploadResult.get("format");

        CloudinaryImage image = new CloudinaryImage();
        image.setPublicId(publicId);
        image.setUrl(url);
        image.setSecureUrl(secureUrl);
        image.setFormat(format);
        image.setCreatedAt(LocalDateTime.now());

        cloudinaryImageRepository.save(image);

        return Map.of(
                "public_id", publicId,
                "url", url,
                "secure_url", secureUrl,
                "format", format,
                "upload_result", uploadResult
        );
    }

    public Map<String, Object> uploadBase64Image(String base64Image) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        Map uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.emptyMap());

        // Same logic to extract and save
        String publicId = (String) uploadResult.get("public_id");
        String url = (String) uploadResult.get("url");
        String secureUrl = (String) uploadResult.get("secure_url");
        String format = (String) uploadResult.get("format");

        CloudinaryImage image = new CloudinaryImage();
        image.setPublicId(publicId);
        image.setUrl(url);
        image.setSecureUrl(secureUrl);
        image.setFormat(format);
        image.setCreatedAt(LocalDateTime.now());

        cloudinaryImageRepository.save(image);

        return Map.of(
                "public_id", publicId,
                "url", url,
                "secure_url", secureUrl,
                "format", format,
                "upload_result", uploadResult
        );
    }
}
