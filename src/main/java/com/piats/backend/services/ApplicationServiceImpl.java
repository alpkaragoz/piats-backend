package com.piats.backend.services;

import com.piats.backend.dto.ApplicationRequestDto;
import com.piats.backend.dto.ApplicationResponseDto;
import com.piats.backend.dto.DetailedApplicationResponseDto;
import com.piats.backend.dto.InitiateApplicationRequestDto;
import com.piats.backend.dto.InitiateApplicationResponseDto;
import com.piats.backend.dto.ApplicationSummaryResponseDto;
import com.piats.backend.models.*;
import com.piats.backend.repos.*;
import com.piats.backend.repos.specs.ApplicationSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicantRepository applicantRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusRepository applicationStatusRepository;
    private final SkillRepository skillRepository;
    private final JobPostingRepository jobPostingRepository;
    private final ApplicationSpecification applicationSpecification;

    @Override
    @Transactional
    public InitiateApplicationResponseDto initiateApplication(InitiateApplicationRequestDto requestDto) {
        // 1. Create a placeholder applicant to hold the relation
        Applicant placeholderApplicant = new Applicant();
        placeholderApplicant.setEmail("placeholder-" + UUID.randomUUID() + "@piats.com");
        applicantRepository.save(placeholderApplicant);

        // 2. Find the job posting
        JobPosting jobPosting = jobPostingRepository.findById(requestDto.getJobPostId())
                .orElseThrow(() -> new EntityNotFoundException("JobPosting not found with id: " + requestDto.getJobPostId()));
        
        // 3. Find the "Draft" status
        ApplicationStatus draftStatus = applicationStatusRepository.findByName("Draft")
                .orElseThrow(() -> new IllegalStateException("Draft status not found in database."));

        // 4. Create the draft application
        Application application = new Application();
        application.setApplicant(placeholderApplicant);
        application.setJobPosting(jobPosting);
        application.setStatus(draftStatus);
        
        Application savedApplication = applicationRepository.save(application);

        return new InitiateApplicationResponseDto(savedApplication.getId());
    }

    @Override
    public DetailedApplicationResponseDto getApplicationById(UUID id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Application not found with id: " + id));
        return mapApplicationToDetailedResponse(application);
    }

    @Override
    public List<DetailedApplicationResponseDto> getAllApplications(UUID jobPostId, Integer statusId, Integer skillId) {
        Specification<Application> spec = applicationSpecification.isNotDraft()
                .and(applicationSpecification.hasJobPostingId(jobPostId))
                .and(applicationSpecification.hasStatus(statusId))
                .and(applicationSpecification.hasSkill(skillId));

        List<Application> applications = applicationRepository.findAll(spec);
        return applications.stream()
                .map(this::mapApplicationToDetailedResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationSummaryResponseDto> getApplicationsByJobPostingId(UUID jobPostId) {
        if (!jobPostingRepository.existsById(jobPostId)) {
            throw new EntityNotFoundException("JobPosting not found with id: " + jobPostId);
        }
        List<Application> applications = applicationRepository.findByJobPostingIdAndStatus_NameNot(jobPostId, "Draft");
        return applications.stream()
                .map(this::mapApplicationToSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DetailedApplicationResponseDto updateApplicationStatus(UUID applicationId, Integer statusId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found with id: " + applicationId));

        ApplicationStatus status = applicationStatusRepository.findById(statusId)
                .orElseThrow(() -> new EntityNotFoundException("ApplicationStatus not found with id: " + statusId));

        application.setStatus(status);
        Application updatedApplication = applicationRepository.save(application);

        return mapApplicationToDetailedResponse(updatedApplication);
    }

    @Override
    @Transactional
    public DetailedApplicationResponseDto completeApplication(UUID applicationId, ApplicationRequestDto requestDto) {
        // 1. Find the existing draft application
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found with id: " + applicationId));

        // 2. Find and update the placeholder applicant with real data
        Applicant applicant = application.getApplicant();
        ApplicationRequestDto.ApplicantDto applicantDto = requestDto.getApplicant();
        applicant.setFirstName(applicantDto.getFirstName());
        applicant.setLastName(applicantDto.getLastName());
        applicant.setEmail(applicantDto.getEmail());
        applicant.setProfessionalSummary(applicantDto.getProfessionalSummary());
        applicant.setPhone(applicantDto.getPhone());
        applicant.setAddress(applicantDto.getAddress());
        applicant.setCity(applicantDto.getCity());
        applicant.setCountry(applicantDto.getCountry());
        applicant.setPostalCode(applicantDto.getPostalCode());
        applicant.setLinkedInUrl(applicantDto.getLinkedInUrl());
        applicant.setPortfolioUrl(applicantDto.getPortfolioUrl());
        applicantRepository.save(applicant);

        // 3. Update application ranking
        application.setRanking(requestDto.getRanking());

        // 4. Map and associate all detail entities (experience, skills, etc.)
        addDetailsToApplication(application, requestDto);

        // 5. Update the status from "Draft" to "New"
        ApplicationStatus newStatus = applicationStatusRepository.findByName("New")
                .orElseThrow(() -> new IllegalStateException("New status not found in database."));
        application.setStatus(newStatus);
        
        Application savedApplication = applicationRepository.save(application);

        return mapApplicationToDetailedResponse(savedApplication);
    }

    private void addDetailsToApplication(Application application, ApplicationRequestDto requestDto) {
        // Experiences
        if (requestDto.getExperiences() != null) {
            application.getExperiences().clear();
            requestDto.getExperiences().stream().map(dto -> {
                Experience exp = new Experience();
                exp.setApplication(application);
                exp.setJobTitle(dto.getJobTitle());
                exp.setCompanyName(dto.getCompanyName());
                exp.setDescription(dto.getDescription());
                exp.setLocation(dto.getLocation());
                exp.setStartDate(dto.getStartDate());
                exp.setEndDate(dto.getEndDate());
                return exp;
            }).forEach(application.getExperiences()::add);
        }

        // Education
        if (requestDto.getEducations() != null) {
            application.getEducations().clear();
            application.getEducations().addAll(requestDto.getEducations().stream().map(dto -> {
                Education edu = new Education();
                edu.setApplication(application);
                edu.setDegree(dto.getDegree());
                edu.setInstitution(dto.getInstitution());
                edu.setFieldOfStudy(dto.getFieldOfStudy());
                edu.setStartDate(dto.getStartDate());
                edu.setEndDate(dto.getEndDate());
                return edu;
            }).collect(Collectors.toSet()));
        }
        
        // Languages
        if (requestDto.getLanguages() != null) {
            application.getLanguages().clear();
            application.getLanguages().addAll(requestDto.getLanguages().stream().map(dto -> {
                Language lang = new Language();
                lang.setApplication(application);
                lang.setLanguage(dto.getLanguage());
                lang.setCefrLevel(dto.getCefrLevel());
                return lang;
            }).collect(Collectors.toSet()));
        }

        // Projects
        if (requestDto.getProjects() != null) {
            application.getProjects().clear();
            application.getProjects().addAll(requestDto.getProjects().stream().map(dto -> {
                Project proj = new Project();
                proj.setApplication(application);
                proj.setName(dto.getName());
                proj.setDescription(dto.getDescription());
                proj.setRole(dto.getRole());
                proj.setTechnologies(dto.getTechnologies());
                proj.setStartDate(dto.getStartDate());
                proj.setEndDate(dto.getEndDate());
                proj.setUrl(dto.getUrl());
                return proj;
            }).collect(Collectors.toSet()));
        }

        // Certifications
        if (requestDto.getCertifications() != null) {
            application.getCertifications().clear();
            application.getCertifications().addAll(requestDto.getCertifications().stream().map(dto -> {
                Certification cert = new Certification();
                cert.setApplication(application);
                cert.setName(dto.getName());
                cert.setIssuer(dto.getIssuer());
                cert.setIssueDate(dto.getIssueDate());
                cert.setExpirationDate(dto.getExpirationDate());
                cert.setCredentialId(dto.getCredentialId());
                cert.setCredentialUrl(dto.getCredentialUrl());
                return cert;
            }).collect(Collectors.toSet()));
        }

        // Skills
        if (requestDto.getSkills() != null) {
            application.getSkills().clear();
            application.getSkills().addAll(requestDto.getSkills().stream()
                .filter(dto -> dto.getSkillId() != null)
                .map(dto -> {
                ApplicationSkill appSkill = new ApplicationSkill();
                appSkill.setApplication(application);
                appSkill.setYearsOfExperience(dto.getYearsOfExperience());
                Skill skill = skillRepository.findById(dto.getSkillId())
                        .orElseThrow(() -> new EntityNotFoundException("Skill not found with id: " + dto.getSkillId()));
                appSkill.setSkill(skill);
                return appSkill;
            }).collect(Collectors.toSet()));
        }
    }

    private DetailedApplicationResponseDto mapApplicationToDetailedResponse(Application application) {
        DetailedApplicationResponseDto dto = new DetailedApplicationResponseDto();
        dto.setId(application.getId());
        dto.setRanking(application.getRanking());
        dto.setAppliedAt(application.getAppliedAt());

        if (application.getStatus() != null) {
            dto.setStatus(application.getStatus().getName());
        }

        // Map JobPosting Info
        DetailedApplicationResponseDto.JobInfo jobInfo = new DetailedApplicationResponseDto.JobInfo();
        jobInfo.setId(application.getJobPosting().getId());
        jobInfo.setTitle(application.getJobPosting().getTitle());
        dto.setJobPosting(jobInfo);

        // Map Applicant
        Applicant applicant = application.getApplicant();
        DetailedApplicationResponseDto.ApplicantDto applicantDto = new DetailedApplicationResponseDto.ApplicantDto();
        applicantDto.setId(applicant.getId());
        applicantDto.setFirstName(applicant.getFirstName());
        applicantDto.setLastName(applicant.getLastName());
        applicantDto.setEmail(applicant.getEmail());
        applicantDto.setProfessionalSummary(applicant.getProfessionalSummary());
        applicantDto.setPhone(applicant.getPhone());
        applicantDto.setAddress(applicant.getAddress());
        applicantDto.setCity(applicant.getCity());
        applicantDto.setCountry(applicant.getCountry());
        applicantDto.setPostalCode(applicant.getPostalCode());
        applicantDto.setLinkedInUrl(applicant.getLinkedInUrl());
        applicantDto.setPortfolioUrl(applicant.getPortfolioUrl());
        dto.setApplicant(applicantDto);

        // Map Collections
        dto.setExperiences(application.getExperiences().stream().map(this::mapExperience).collect(Collectors.toList()));
        dto.setEducations(application.getEducations().stream().map(this::mapEducation).collect(Collectors.toList()));
        dto.setLanguages(application.getLanguages().stream().map(this::mapLanguage).collect(Collectors.toList()));
        dto.setProjects(application.getProjects().stream().map(this::mapProject).collect(Collectors.toList()));
        dto.setCertifications(application.getCertifications().stream().map(this::mapCertification).collect(Collectors.toList()));
        dto.setSkills(application.getSkills().stream().map(this::mapApplicationSkill).collect(Collectors.toList()));

        return dto;
    }

    private DetailedApplicationResponseDto.ExperienceDto mapExperience(Experience exp) {
        DetailedApplicationResponseDto.ExperienceDto dto = new DetailedApplicationResponseDto.ExperienceDto();
        dto.setId(exp.getId());
        dto.setJobTitle(exp.getJobTitle());
        dto.setCompanyName(exp.getCompanyName());
        dto.setDescription(exp.getDescription());
        dto.setLocation(exp.getLocation());
        dto.setStartDate(exp.getStartDate());
        dto.setEndDate(exp.getEndDate());
        return dto;
    }

    private DetailedApplicationResponseDto.EducationDto mapEducation(Education edu) {
        DetailedApplicationResponseDto.EducationDto dto = new DetailedApplicationResponseDto.EducationDto();
        dto.setId(edu.getId());
        dto.setDegree(edu.getDegree());
        dto.setInstitution(edu.getInstitution());
        dto.setFieldOfStudy(edu.getFieldOfStudy());
        dto.setStartDate(edu.getStartDate());
        dto.setEndDate(edu.getEndDate());
        return dto;
    }

    private DetailedApplicationResponseDto.LanguageDto mapLanguage(Language lang) {
        DetailedApplicationResponseDto.LanguageDto dto = new DetailedApplicationResponseDto.LanguageDto();
        dto.setId(lang.getId());
        dto.setLanguage(lang.getLanguage());
        dto.setCefrLevel(lang.getCefrLevel());
        return dto;
    }

    private DetailedApplicationResponseDto.ProjectDto mapProject(Project proj) {
        DetailedApplicationResponseDto.ProjectDto dto = new DetailedApplicationResponseDto.ProjectDto();
        dto.setId(proj.getId());
        dto.setName(proj.getName());
        dto.setDescription(proj.getDescription());
        dto.setRole(proj.getRole());
        dto.setTechnologies(proj.getTechnologies());
        dto.setStartDate(proj.getStartDate());
        dto.setEndDate(proj.getEndDate());
        dto.setUrl(proj.getUrl());
        return dto;
    }

    private DetailedApplicationResponseDto.CertificationDto mapCertification(Certification cert) {
        DetailedApplicationResponseDto.CertificationDto dto = new DetailedApplicationResponseDto.CertificationDto();
        dto.setId(cert.getId());
        dto.setName(cert.getName());
        dto.setIssuer(cert.getIssuer());
        dto.setIssueDate(cert.getIssueDate());
        dto.setExpirationDate(cert.getExpirationDate());
        dto.setCredentialId(cert.getCredentialId());
        dto.setCredentialUrl(cert.getCredentialUrl());
        return dto;
    }

    private DetailedApplicationResponseDto.ApplicationSkillDto mapApplicationSkill(ApplicationSkill appSkill) {
        DetailedApplicationResponseDto.ApplicationSkillDto dto = new DetailedApplicationResponseDto.ApplicationSkillDto();
        dto.setId(appSkill.getId());
        dto.setYearsOfExperience(appSkill.getYearsOfExperience());
        if (appSkill.getSkill() != null) {
            dto.setSkillName(appSkill.getSkill().getName());
        }
        return dto;
    }

    private ApplicationSummaryResponseDto mapApplicationToSummaryResponse(Application application) {
        ApplicationSummaryResponseDto dto = new ApplicationSummaryResponseDto();
        dto.setApplicationId(application.getId());
        dto.setRanking(application.getRanking());
        dto.setAppliedAt(application.getAppliedAt());

        if (application.getStatus() != null) {
            dto.setStatus(application.getStatus().getName());
        }

        Applicant applicant = application.getApplicant();
        if (applicant != null) {
            dto.setApplicantName(applicant.getFirstName() + " " + applicant.getLastName());
            dto.setApplicantEmail(applicant.getEmail());
            dto.setApplicantPhone(applicant.getPhone());
            dto.setProfessionalSummary(applicant.getProfessionalSummary());
            dto.setAddress(applicant.getAddress());
            dto.setCity(applicant.getCity());
            dto.setCountry(applicant.getCountry());
            dto.setPostalCode(applicant.getPostalCode());
            dto.setLinkedInUrl(applicant.getLinkedInUrl());
            dto.setPortfolioUrl(applicant.getPortfolioUrl());
        }

        return dto;
    }
} 