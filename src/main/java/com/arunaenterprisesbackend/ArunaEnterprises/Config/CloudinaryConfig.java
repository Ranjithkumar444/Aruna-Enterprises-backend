package com.arunaenterprisesbackend.ArunaEnterprises.Config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dolgydyvd");
        config.put("api_key", "591432157122123");
        config.put("api_secret", "R5C6UdCOkJSEr33RGXkS3fwuTkc");
        return new Cloudinary(config);
    }
}
