package com.piats.backend.repos;

import com.piats.backend.models.ApplicationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ApplicationStatusRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApplicationStatusRepository applicationStatusRepository;

    private ApplicationStatus pendingStatus;
    private ApplicationStatus approvedStatus;

    @BeforeEach
    void setUp() {
        pendingStatus = new ApplicationStatus();
        pendingStatus.setName("PENDING");

        approvedStatus = new ApplicationStatus();
        approvedStatus.setName("APPROVED");
    }

    @Test
    void findByName_WhenStatusExists_ShouldReturnStatus() {
        // Given
        entityManager.persistAndFlush(pendingStatus);

        // When
        Optional<ApplicationStatus> result = applicationStatusRepository.findByName("PENDING");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("PENDING");
    }

    @Test
    void findByName_WhenStatusDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<ApplicationStatus> result = applicationStatusRepository.findByName("NON_EXISTENT");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByName_WithMultipleStatuses_ShouldReturnCorrectOne() {
        // Given
        entityManager.persistAndFlush(pendingStatus);
        entityManager.persistAndFlush(approvedStatus);

        // When
        Optional<ApplicationStatus> pendingResult = applicationStatusRepository.findByName("PENDING");
        Optional<ApplicationStatus> approvedResult = applicationStatusRepository.findByName("APPROVED");

        // Then
        assertThat(pendingResult).isPresent();
        assertThat(pendingResult.get().getName()).isEqualTo("PENDING");
        
        assertThat(approvedResult).isPresent();
        assertThat(approvedResult.get().getName()).isEqualTo("APPROVED");
    }
}
