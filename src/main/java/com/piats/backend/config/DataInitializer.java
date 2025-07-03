package com.piats.backend.config;

import com.piats.backend.models.ApplicationStatus;
import com.piats.backend.models.Skill;
import com.piats.backend.models.User;
import com.piats.backend.repos.ApplicationStatusRepository;
import com.piats.backend.repos.SkillRepository;
import com.piats.backend.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.piats.backend.enums.Role;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ApplicationStatusRepository applicationStatusRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeApplicationStatuses();
        initializeSkills();
        initializeDefaultUser();
        initializeTechnicalLead();
    }

    /**
     * Initializes a default recruiter if one doesn't exist.
     */
    private void initializeDefaultUser() {
        String defaultUserEmail = "recruiter@piats.com";
        if (userRepository.findByEmail(defaultUserEmail).isEmpty()) {
            log.info("Recruiter not found. Creating...");
            User defaultUser = new User();
            defaultUser.setEmail(defaultUserEmail);
            defaultUser.setPassword(passwordEncoder.encode("123456"));
            defaultUser.setFirstName("Joe");
            defaultUser.setLastName("Doe");
            defaultUser.setRole(Role.RECRUITER);
            userRepository.save(defaultUser);
            log.info("Recruiter created with email: {}", defaultUserEmail);
        } else {
            log.info("Recruiter already exists. Skipping initialization.");
        }
    }

    /**
     * Initializes a default Technical Manager if one doesn't exist.
     */
    private void initializeTechnicalLead() {
        String managerEmail = "technicallead@piats.com";
        if (userRepository.findByEmail(managerEmail).isEmpty()) {
            log.info("Technical Lead not found. Creating...");
            User techManager = new User();
            techManager.setEmail(managerEmail);
            techManager.setPassword(passwordEncoder.encode("123456"));
            techManager.setFirstName("Jane");
            techManager.setLastName("Smith");
            techManager.setRole(Role.TECHNICAL_LEAD);
            userRepository.save(techManager);
            log.info("Technical Lead created with email: {}", managerEmail);
        } else {
            log.info("Technical Lead already exists. Skipping initialization.");
        }
    }

    private void initializeApplicationStatuses() {
        if (applicationStatusRepository.count() == 0) {
            log.info("No application statuses found. Initializing default statuses...");
            List<String> statuses = Arrays.asList(
                "Received",
                "Under Review",
                "Interviewing",
                "Offered",
                "Hired",
                "Rejected"
            );
            
            statuses.forEach(statusName -> {
                ApplicationStatus status = new ApplicationStatus();
                status.setName(statusName);
                applicationStatusRepository.save(status);
            });
            log.info("Default application statuses have been initialized.");
        } else {
            log.info("Application statuses already exist. Skipping initialization.");
        }
    }

    private void initializeSkills() {
        if (skillRepository.count() == 0) {
            log.info("No skills found. Initializing default skills...");
            List<String> skills = Arrays.asList(
                "Java", "Spring Boot", "PostgreSQL", "Docker", "Kubernetes",
                "JavaScript", "React", "Node.js", "Python", "Git"
            );

            skills.forEach(skillName -> {
                Skill skill = new Skill();
                skill.setName(skillName);
                skillRepository.save(skill);
            });
            log.info("Default skills have been initialized.");
        } else {
            log.info("Skills already exist. Skipping initialization.");
        }
    }
} 