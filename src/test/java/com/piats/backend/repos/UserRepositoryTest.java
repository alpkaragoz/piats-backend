package com.piats.backend.repos;

import com.piats.backend.enums.Role;
import com.piats.backend.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("hashedPassword123");
        testUser.setRole(Role.RECRUITER);
    }

    @Test
    void findByEmail_WhenUserExists_ShouldReturnUser() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> result = userRepository.findByEmail("john.doe@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.get().getFirstName()).isEqualTo("John");
        assertThat(result.get().getLastName()).isEqualTo("Doe");
    }

    @Test
    void findByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByRole_WhenUsersWithRoleExist_ShouldReturnPagedResults() {
        // Given
        User recruiter1 = new User();
        recruiter1.setEmail("recruiter1@example.com");
        recruiter1.setPassword("password");
        recruiter1.setRole(Role.RECRUITER);
        
        User recruiter2 = new User();
        recruiter2.setEmail("recruiter2@example.com");
        recruiter2.setPassword("password");
        recruiter2.setRole(Role.RECRUITER);
        
        User techLead = new User();
        techLead.setEmail("techlead@example.com");
        techLead.setPassword("password");
        techLead.setRole(Role.TECHNICAL_LEAD);

        entityManager.persistAndFlush(recruiter1);
        entityManager.persistAndFlush(recruiter2);
        entityManager.persistAndFlush(techLead);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByRole(Role.RECRUITER, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(User::getRole)
                .containsOnly(Role.RECRUITER);
    }

    @Test
    void findByRole_WhenNoUsersWithRole_ShouldReturnEmptyPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userRepository.findByRole(Role.TECHNICAL_LEAD, pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }
}
