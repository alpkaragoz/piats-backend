package com.piats.backend.repos;

import com.piats.backend.enums.EmploymentType;
import com.piats.backend.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ApplicationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApplicationRepository applicationRepository;

    private JobPosting jobPosting;
    private Applicant applicant;
    private ApplicationStatus pendingStatus;
    private ApplicationStatus rejectedStatus;
    private Application application1;
    private Application application2;

    @BeforeEach
    void setUp() {
        // Create test entities
        jobPosting = new JobPosting();
        jobPosting.setTitle("Software Developer");
        jobPosting.setDescription("Java developer position");
        jobPosting.setLocation("Remote");
        jobPosting.setEmploymentType(EmploymentType.FULL_TIME);
        entityManager.persistAndFlush(jobPosting);

        applicant = new Applicant();
        applicant.setFirstName("Jane");
        applicant.setLastName("Smith");
        applicant.setEmail("jane.smith@example.com");
        entityManager.persistAndFlush(applicant);

        pendingStatus = new ApplicationStatus();
        pendingStatus.setName("PENDING");
        entityManager.persistAndFlush(pendingStatus);

        rejectedStatus = new ApplicationStatus();
        rejectedStatus.setName("REJECTED");
        entityManager.persistAndFlush(rejectedStatus);

        application1 = new Application();
        application1.setJobPosting(jobPosting);
        application1.setApplicant(applicant);
        application1.setStatus(pendingStatus);
        application1.setRanking(1);

        application2 = new Application();
        application2.setJobPosting(jobPosting);
        application2.setApplicant(applicant);
        application2.setStatus(rejectedStatus);
        application2.setRanking(2);
    }

    @Test
    void findByJobPostingId_WhenApplicationsExist_ShouldReturnAllApplications() {
        // Given
        entityManager.persistAndFlush(application1);
        entityManager.persistAndFlush(application2);

        // When
        List<Application> result = applicationRepository.findByJobPostingId(jobPosting.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Application::getJobPosting)
                .extracting(JobPosting::getId)
                .containsOnly(jobPosting.getId());
    }

    @Test
    void findByJobPostingId_WhenNoApplicationsExist_ShouldReturnEmptyList() {
        // Given
        UUID nonExistentJobId = UUID.randomUUID();

        // When
        List<Application> result = applicationRepository.findByJobPostingId(nonExistentJobId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByJobPostingIdAndStatus_NameNot_ShouldExcludeSpecifiedStatus() {
        // Given
        entityManager.persistAndFlush(application1);
        entityManager.persistAndFlush(application2);

        // When
        List<Application> result = applicationRepository.findByJobPostingIdAndStatus_NameNot(
                jobPosting.getId(), "REJECTED");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus().getName()).isEqualTo("PENDING");
        assertThat(result.get(0).getStatus().getName()).isNotEqualTo("REJECTED");
    }
}
