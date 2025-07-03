package com.piats.backend.services;

import com.piats.backend.dto.ApplicationRequestDto;
import com.piats.backend.dto.ApplicationResponseDto;
import com.piats.backend.dto.DetailedApplicationResponseDto;
import com.piats.backend.models.*;
import com.piats.backend.repos.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    @Override
    @Transactional
    public ApplicationResponseDto createApplication(ApplicationRequestDto requestDto) {
        // 1. Create and save the Applicant
        Applicant applicant = new Applicant();
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
        applicant = applicantRepository.save(applicant);

        // 2. Create the main Application entity
        Application application = new Application();
        application.setApplicant(applicant);

        JobPosting jobPosting = jobPostingRepository.findById(requestDto.getJobPostId())
                .orElseThrow(() -> new EntityNotFoundException("JobPosting not found with id: " + requestDto.getJobPostId()));
        application.setJobPosting(jobPosting);
        
        application.setRanking(requestDto.getRanking());

        if (requestDto.getStatusId() != null) {
            ApplicationStatus status = applicationStatusRepository.findById(requestDto.getStatusId())
                    .orElseThrow(() -> new EntityNotFoundException("ApplicationStatus not found with id: " + requestDto.getStatusId()));
            application.setStatus(status);
        }

        // 3. Map and associate all detail entities
        addDetailsToApplication(application, requestDto);

        // 4. Save the application and all cascaded entities
        Application savedApplication = applicationRepository.save(application);

        return new ApplicationResponseDto(savedApplication.getId(), "Application created successfully.");
    }

    @Override
    public DetailedApplicationResponseDto getApplicationById(UUID id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Application not found with id: " + id));
        return mapApplicationToDetailedResponse(application);
    }

    @Override
    public Page<DetailedApplicationResponseDto> getAllApplications(Pageable pageable) {
        Page<Application> applications = applicationRepository.findAll(pageable);
        return applications.map(this::mapApplicationToDetailedResponse);
    }

    @Override
    public Page<DetailedApplicationResponseDto> getApplicationsByJobPostingId(UUID jobPostId, Pageable pageable) {
        if (!jobPostingRepository.existsById(jobPostId)) {
            throw new EntityNotFoundException("JobPosting not found with id: " + jobPostId);
        }
        Page<Application> applications = applicationRepository.findByJobPostingId(jobPostId, pageable);
        return applications.map(this::mapApplicationToDetailedResponse);
    }

    private void addDetailsToApplication(Application application, ApplicationRequestDto requestDto) {
        // Experiences
        if (requestDto.getExperiences() != null) {
            application.setExperiences(requestDto.getExperiences().stream().map(dto -> {
                Experience exp = new Experience();
                exp.setApplication(application);
                exp.setJobTitle(dto.getJobTitle());
                exp.setCompanyName(dto.getCompanyName());
                exp.setDescription(dto.getDescription());
                exp.setLocation(dto.getLocation());
                exp.setStartDate(dto.getStartDate());
                exp.setEndDate(dto.getEndDate());
                return exp;
            }).collect(Collectors.toSet()));
        }

        // Education
        if (requestDto.getEducations() != null) {
            application.setEducations(requestDto.getEducations().stream().map(dto -> {
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
            application.setLanguages(requestDto.getLanguages().stream().map(dto -> {
                Language lang = new Language();
                lang.setApplication(application);
                lang.setLanguage(dto.getLanguage());
                lang.setCefrLevel(dto.getCefrLevel());
                return lang;
            }).collect(Collectors.toSet()));
        }

        // Projects
        if (requestDto.getProjects() != null) {
            application.setProjects(requestDto.getProjects().stream().map(dto -> {
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
            application.setCertifications(requestDto.getCertifications().stream().map(dto -> {
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
            application.setSkills(requestDto.getSkills().stream().map(dto -> {
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
} 