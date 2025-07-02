package com.piats.backend.config;

import com.piats.backend.models.ApplicationStatus;
import com.piats.backend.models.Skill;
import com.piats.backend.repos.ApplicationStatusRepository;
import com.piats.backend.repos.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ApplicationStatusRepository applicationStatusRepository;
    private final SkillRepository skillRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeApplicationStatuses();
        initializeSkills();
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