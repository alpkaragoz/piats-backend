package com.piats.backend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "application_experience")
@Data
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(length = 255)
    private String jobTitle;

    @Column(length = 255)
    private String companyName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String location;

    private LocalDate startDate;
    private LocalDate endDate;
} 