package com.piats.backend.services;

import com.piats.backend.dto.JobPostingDto;
import com.piats.backend.enums.EmploymentType;
import com.piats.backend.enums.ExperienceLevel;
import com.piats.backend.enums.Role;
import com.piats.backend.models.JobPosting;
import com.piats.backend.models.JobPostingStatus;
import com.piats.backend.models.User;
import com.piats.backend.repos.JobPostingRepository;
import com.piats.backend.repos.JobPostingStatusRepository;
import com.piats.backend.repos.UserRepository;
import com.piats.backend.repos.specs.JobPostingSpecification;
import com.piats.backend.utils.AuthUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobPostingServiceTest {

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private JobPostingStatusRepository jobPostingStatusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthUtil authUtil;

    @InjectMocks
    private JobPostingServiceImpl jobPostingService;

    private JobPosting jobPosting;
    private JobPostingStatus activeStatus;
    private User user;
    private JobPostingDto.JobPostingRequest request;

    @BeforeEach
    void setUp() {
        activeStatus = new JobPostingStatus();
        activeStatus.setId(1);
        activeStatus.setName("ACTIVE");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setRole(Role.RECRUITER);

        jobPosting = new JobPosting();
        jobPosting.setId(UUID.randomUUID());
        jobPosting.setTitle("Software Developer");
        jobPosting.setDescription("Java developer position");
        jobPosting.setLocation("Remote");
        jobPosting.setEmploymentType(EmploymentType.FULL_TIME);
        jobPosting.setExperienceLevel(ExperienceLevel.MID_LEVEL);
        jobPosting.setStatus(activeStatus);
        jobPosting.setCreatedBy(user);

        request = new JobPostingDto.JobPostingRequest();
        request.setTitle("Software Developer");
        request.setDescription("Java developer position");
        request.setLocation("Remote");
        request.setEmploymentType(EmploymentType.FULL_TIME);
        request.setExperienceLevel(ExperienceLevel.MID_LEVEL);
        request.setStatusId(1);
        request.setCreatedById(user.getId());
    }

    @Test
    void createJobPosting_WhenValidRequest_ShouldCreateJobPosting() {
        // Given
        when(jobPostingStatusRepository.findById(1)).thenReturn(Optional.of(activeStatus));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(jobPostingRepository.save(any(JobPosting.class))).thenReturn(jobPosting);

        // When
        JobPostingDto.JobPostingResponse result = jobPostingService.createJobPosting(request, "header");

        // Then
        assertThat(result).isNotNull();
        verify(jobPostingRepository).save(any(JobPosting.class));
    }

    @Test
    void getJobPostingById_WhenJobPostingExists_ShouldReturnJobPosting() {
        // Given
        UUID jobPostingId = jobPosting.getId();
        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPosting));

        // When
        JobPostingDto.JobPostingResponse result = jobPostingService.getJobPostingById(jobPostingId);

        // Then
        assertThat(result).isNotNull();
        verify(jobPostingRepository).findById(jobPostingId);
    }

    @Test
    void getJobPostingById_WhenJobPostingNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        UUID jobPostingId = UUID.randomUUID();
        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> jobPostingService.getJobPostingById(jobPostingId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("JobPosting not found with id:");
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAllJobPostings_WhenJobPostingsExist_ShouldReturnJobPostings() {
        // Given
        List<JobPosting> jobPostings = Arrays.asList(jobPosting);
        
        try (MockedStatic<JobPostingSpecification> mockedSpec = mockStatic(JobPostingSpecification.class)) {
            Specification<JobPosting> mockSpec = mock(Specification.class);
            mockedSpec.when(() -> JobPostingSpecification.hasTitle("Java")).thenReturn(mockSpec);
            when(mockSpec.and(any())).thenReturn(mockSpec);
            when(jobPostingRepository.findAll(any(Specification.class))).thenReturn(jobPostings);

            // When
            List<JobPostingDto.JobPostingResponse> result = jobPostingService.getAllJobPostings("Java", 1);

            // Then
            assertThat(result).isNotNull();
            verify(jobPostingRepository).findAll(any(Specification.class));
        }
    }

    @Test
    void updateJobPosting_WhenJobPostingExists_ShouldUpdateJobPosting() {
        // Given
        UUID jobPostingId = jobPosting.getId();
        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPosting));
        when(jobPostingStatusRepository.findById(1)).thenReturn(Optional.of(activeStatus));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(jobPostingRepository.save(any(JobPosting.class))).thenReturn(jobPosting);

        // When
        JobPostingDto.JobPostingResponse result = jobPostingService.updateJobPosting(jobPostingId, request, "header");

        // Then
        assertThat(result).isNotNull();
        verify(jobPostingRepository).findById(jobPostingId);
        verify(jobPostingRepository).save(any(JobPosting.class));
    }

    @Test
    void updateJobPosting_WhenJobPostingNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        UUID jobPostingId = UUID.randomUUID();
        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> jobPostingService.updateJobPosting(jobPostingId, request, "header"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("JobPosting not found with id:");
    }

    @Test
    void deleteJobPosting_WhenJobPostingExists_ShouldDeleteJobPosting() {
        // Given
        UUID jobPostingId = jobPosting.getId();
        when(jobPostingRepository.existsById(jobPostingId)).thenReturn(true);

        // When
        jobPostingService.deleteJobPosting(jobPostingId, "header");

        // Then
        verify(jobPostingRepository).deleteById(jobPostingId);
    }

    @Test
    void deleteJobPosting_WhenJobPostingNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        UUID jobPostingId = UUID.randomUUID();
        when(jobPostingRepository.existsById(jobPostingId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> jobPostingService.deleteJobPosting(jobPostingId, "header"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("JobPosting not found with id:");
    }

    @Test
    void updateJobPostingStatus_WhenJobPostingAndStatusExist_ShouldUpdateStatus() {
        // Given
        UUID jobPostingId = jobPosting.getId();
        Integer statusId = activeStatus.getId();

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPosting));
        when(jobPostingStatusRepository.findById(statusId)).thenReturn(Optional.of(activeStatus));
        when(jobPostingRepository.save(any(JobPosting.class))).thenReturn(jobPosting);

        // When
        JobPostingDto.JobPostingResponse result = jobPostingService.updateJobPostingStatus(jobPostingId, statusId);

        // Then
        assertThat(result).isNotNull();
        verify(jobPostingRepository).findById(jobPostingId);
        verify(jobPostingStatusRepository).findById(statusId);
        verify(jobPostingRepository).save(jobPosting);
    }

    @Test
    void updateJobPostingStatus_WhenStatusNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        UUID jobPostingId = jobPosting.getId();
        Integer statusId = 999;

        when(jobPostingRepository.findById(jobPostingId)).thenReturn(Optional.of(jobPosting));
        when(jobPostingStatusRepository.findById(statusId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> jobPostingService.updateJobPostingStatus(jobPostingId, statusId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("JobPostingStatus not found with id:");
    }
}
