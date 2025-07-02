package com.piats.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ApplicationRequestDto {
    private ApplicantDto applicant;
    private List<ExperienceDto> experiences;
    private List<EducationDto> educations;
    private List<LanguageDto> languages;
    private List<ProjectDto> projects;
    private List<CertificationDto> certifications;
    private List<ApplicationSkillDto> skills;
    private Integer statusId;
    private Integer ranking;

    @Data
    public static class ApplicantDto {
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
        private String jobTitle;
        private String companyName;
        private String description;
        private String location;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Data
    public static class EducationDto {
        private String degree;
        private String institution;
        private String fieldOfStudy;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Data
    public static class LanguageDto {
        private String language;
        private String cefrLevel;
    }

    @Data
    public static class ProjectDto {
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
        private String name;
        private String issuer;
        private LocalDate issueDate;
        private LocalDate expirationDate;
        private String credentialId;
        private String credentialUrl;
    }

    @Data
    public static class ApplicationSkillDto {
        private Integer skillId;
        private Integer yearsOfExperience;
    }
} 