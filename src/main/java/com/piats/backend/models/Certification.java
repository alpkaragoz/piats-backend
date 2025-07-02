package com.piats.backend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "application_certifications")
@Data
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(length = 255)
    private String name;

    @Column(length = 255)
    private String issuer;

    private LocalDate issueDate;
    private LocalDate expirationDate;

    @Column(length = 255)
    private String credentialId;

    @Column(length = 2048)
    private String credentialUrl;
} 