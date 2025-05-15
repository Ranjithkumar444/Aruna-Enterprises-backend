package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "cloudinary_images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CloudinaryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private String publicId;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "secure_url", length = 500)
    private String secureUrl;

    @Column(length = 50)
    private String format;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
