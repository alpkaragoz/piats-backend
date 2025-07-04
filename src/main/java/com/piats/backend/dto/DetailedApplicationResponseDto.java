package com.piats.backend.dto;

import com.piats.backend.enums.ExperienceLevel;
import lombok.Data;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class DetailedApplicationResponseDto {
    private UUID id;
    private JobInfo jobPosting;
    private ApplicantDto applicant;
    private String status;
    private Integer ranking;
    private ZonedDateTime appliedAt;
    private List<ExperienceDto> experiences;
    private List<EducationDto> educations;
    private List<LanguageDto> languages;
    private List<ProjectDto> projects;
    private List<CertificationDto> certifications;
    private List<ApplicationSkillDto> skills;

    @Data
    public static class JobInfo {
        private UUID id;
        private String title;
    }

    @Data
    public static class ApplicantDto {
        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
        private String professionalSummary;
        private String phone;
        private String address;
        private String city;
        private String country;
        private String postalCode;
        private String linkedInUrl;
        private String portfolioUrl;
    }

    @Data
    public static class ExperienceDto {
        private UUID id;
        private String jobTitle;
        private String companyName;
        private String description;
        private String location;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Data
    public static class EducationDto {
        private UUID id;
        private String degree;
        private String institution;
        private String fieldOfStudy;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Data
    public static class LanguageDto {
        private UUID id;
        private String language;
        private String cefrLevel;
    }

    @Data
    public static class ProjectDto {
        private UUID id;
        private String name;
        private String description;
        private String role;
        private String technologies;
        private LocalDate startDate;
        private LocalDate endDate;
        private String url;
    }

    @Data
    public static class CertificationDto {
        private UUID id;
        private String name;
        private String issuer;
        private LocalDate issueDate;
        private LocalDate expirationDate;
        private String credentialId;
        private String credentialUrl;
    }

    @Data
    public static class ApplicationSkillDto {
        private UUID id;
        private String skillName;
        private Integer yearsOfExperience;
    }
} 