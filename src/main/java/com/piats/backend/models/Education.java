package com.piats.backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "application_education")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Application application;

    @Column(length = 255)
    private String degree;

    @Column(length = 255)
    private String institution;

    @Column(length = 255)
    private String fieldOfStudy;

    private LocalDate startDate;
    private LocalDate endDate;
} 