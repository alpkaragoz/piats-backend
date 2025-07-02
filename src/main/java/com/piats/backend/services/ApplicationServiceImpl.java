package com.piats.backend.services;

import com.piats.backend.dto.ApplicationRequestDto;
import com.piats.backend.dto.ApplicationResponseDto;
import com.piats.backend.models.*;
import com.piats.backend.repos.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicantRepository applicantRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusRepository applicationStatusRepository;
    private final SkillRepository skillRepository;

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
        application.setJobPostId(Application.JOB_POST_ID); // Using static job post ID
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
} 