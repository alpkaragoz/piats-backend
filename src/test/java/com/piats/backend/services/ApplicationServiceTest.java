package com.piats.backend.services;

import com.piats.backend.dto.ApplicationSummaryResponseDto;
import com.piats.backend.dto.DetailedApplicationResponseDto;
import com.piats.backend.dto.InitiateApplicationRequestDto;
import com.piats.backend.dto.InitiateApplicationResponseDto;
import com.piats.backend.enums.EmploymentType;
import com.piats.backend.models.*;
import com.piats.backend.repos.*;
import com.piats.backend.repos.specs.ApplicationSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicantRepository applicantRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationStatusRepository applicationStatusRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private ApplicationSpecification applicationSpecification;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private JobPosting jobPosting;
    private ApplicationStatus draftStatus;
    private ApplicationStatus pendingStatus;
    private Application application;
    private Applicant applicant;

    @BeforeEach
    void setUp() {
        jobPosting = new JobPosting();
        jobPosting.setId(UUID.randomUUID());
        jobPosting.setTitle("Software Developer");
        jobPosting.setDescription("Java developer position");
        jobPosting.setLocation("Remote");
        jobPosting.setEmploymentType(EmploymentType.FULL_TIME);

        draftStatus = new ApplicationStatus();
        draftStatus.setId(1);
        draftStatus.setName("Draft");

        pendingStatus = new ApplicationStatus();
        pendingStatus.setId(2);
        pendingStatus.setName("PENDING");

        applicant = new Applicant();
        applicant.setId(UUID.randomUUID());
        applicant.setFirstName("John");
        applicant.setLastName("Doe");
        applicant.setEmail("john.doe@example.com");

        application = new Application();
        application.setId(UUID.randomUUID());
        application.setJobPosting(jobPosting);
        application.setApplicant(applicant);
        application.setStatus(pendingStatus);
    }

    @Test
    void initiateApplication_WhenJobPostingExists_ShouldCreateDraftApplication() {
        // Given
        InitiateApplicationRequestDto requestDto = new InitiateApplicationRequestDto();
        requestDto.setJobPostId(jobPosting.getId());

        Applicant savedApplicant = new Applicant();
        savedApplicant.setId(UUID.randomUUID());

        Application savedApplication = new Application();
        savedApplication.setId(UUID.randomUUID());

        when(jobPostingRepository.findById(jobPosting.getId())).thenReturn(Optional.of(jobPosting));
        when(applicationStatusRepository.findByName("Draft")).thenReturn(Optional.of(draftStatus));
        when(applicantRepository.save(any(Applicant.class))).thenReturn(savedApplicant);
        when(applicationRepository.save(any(Application.class))).thenReturn(savedApplication);

        // When
        InitiateApplicationResponseDto result = applicationService.initiateApplication(requestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getApplicationId()).isEqualTo(savedApplication.getId());
        verify(applicantRepository).save(any(Applicant.class));
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void initiateApplication_WhenJobPostingNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        InitiateApplicationRequestDto requestDto = new InitiateApplicationRequestDto();
        requestDto.setJobPostId(UUID.randomUUID());

        when(jobPostingRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> applicationService.initiateApplication(requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("JobPosting not found with id:");
    }

    @Test
    void getApplicationById_WhenApplicationExists_ShouldReturnApplication() {
        // Given
        UUID applicationId = UUID.randomUUID();
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        // When
        DetailedApplicationResponseDto result = applicationService.getApplicationById(applicationId);

        // Then
        assertThat(result).isNotNull();
        // Note: Since mapApplicationToDetailedResponse is private, we can only verify the method was called
        verify(applicationRepository).findById(applicationId);
    }

    @Test
    void getApplicationById_WhenApplicationNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        UUID applicationId = UUID.randomUUID();
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> applicationService.getApplicationById(applicationId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Application not found with id:");
    }

    @Test
    void getApplicationsByJobPostingId_WhenJobPostingExists_ShouldReturnApplications() {
        // Given
        UUID jobPostId = jobPosting.getId();
        Application app1 = new Application();
        app1.setApplicant(applicant);
        app1.setJobPosting(jobPosting);
        app1.setStatus(pendingStatus);

        Application app2 = new Application();
        app2.setApplicant(applicant);
        app2.setJobPosting(jobPosting);
        app2.setStatus(pendingStatus);

        List<Application> applications = Arrays.asList(app1, app2);

        when(jobPostingRepository.existsById(jobPostId)).thenReturn(true);
        when(applicationRepository.findByJobPostingIdAndStatus_NameNot(jobPostId, "Draft"))
                .thenReturn(applications);

        // When
        List<ApplicationSummaryResponseDto> result = applicationService.getApplicationsByJobPostingId(jobPostId);

        // Then
        assertThat(result).isNotNull();
        verify(jobPostingRepository).existsById(jobPostId);
        verify(applicationRepository).findByJobPostingIdAndStatus_NameNot(jobPostId, "Draft");
    }

    @Test
    void getApplicationsByJobPostingId_WhenJobPostingNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        UUID jobPostId = UUID.randomUUID();
        when(jobPostingRepository.existsById(jobPostId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> applicationService.getApplicationsByJobPostingId(jobPostId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("JobPosting not found with id:");
    }

    @Test
    void updateApplicationStatus_WhenApplicationAndStatusExist_ShouldUpdateStatus() {
        // Given
        UUID applicationId = application.getId();
        Integer statusId = pendingStatus.getId();

        Application updatedApplication = new Application();
        updatedApplication.setId(applicationId);
        updatedApplication.setJobPosting(jobPosting);
        updatedApplication.setApplicant(applicant);
        updatedApplication.setStatus(pendingStatus);

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationStatusRepository.findById(statusId)).thenReturn(Optional.of(pendingStatus));
        when(applicationRepository.save(any(Application.class))).thenReturn(updatedApplication);

        // When
        DetailedApplicationResponseDto result = applicationService.updateApplicationStatus(applicationId, statusId);

        // Then
        assertThat(result).isNotNull();
        verify(applicationRepository).findById(applicationId);
        verify(applicationStatusRepository).findById(statusId);
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void updateApplicationStatus_WhenApplicationNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        UUID applicationId = UUID.randomUUID();
        Integer statusId = pendingStatus.getId();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> applicationService.updateApplicationStatus(applicationId, statusId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Application not found with id:");
    }

    @Test
    void updateApplicationStatus_WhenStatusNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        UUID applicationId = application.getId();
        Integer statusId = 999;

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationStatusRepository.findById(statusId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> applicationService.updateApplicationStatus(applicationId, statusId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("ApplicationStatus not found with id:");
    }
}
