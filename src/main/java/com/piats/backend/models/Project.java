package com.piats.backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "application_projects")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Application application;

    @Column(length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String role;

    @Column(columnDefinition = "TEXT")
    private String technologies;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(length = 2048)
    private String url;
} 