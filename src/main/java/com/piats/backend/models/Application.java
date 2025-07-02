package com.piats.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "applications")
@Data
public class Application {

    /**
     * As per instruction, this static UUID will be used for the job_post_id
     * until the JobPostings feature is implemented.
     */
    public static final UUID JOB_POST_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @Column(name = "job_post_id", nullable = false)
    private UUID jobPostId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private ApplicationStatus status;

    private Integer ranking;

    @CreationTimestamp
    @Column(name = "applied_at", updatable = false)
    private ZonedDateTime appliedAt;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ApplicationSkill> skills = new HashSet<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Experience> experiences = new HashSet<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Education> educations = new HashSet<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Language> languages = new HashSet<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Project> projects = new HashSet<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Certification> certifications = new HashSet<>();
}
