package com.piats.backend.controllers;

import com.piats.backend.dto.SkillDto;
import com.piats.backend.dto.StatusDto;
import com.piats.backend.enums.EmploymentType;
import com.piats.backend.enums.ExperienceLevel;
import com.piats.backend.repos.ApplicationStatusRepository;
import com.piats.backend.repos.JobPostingStatusRepository;
import com.piats.backend.repos.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/lookups")
@RequiredArgsConstructor
public class LookupController {

    private final ApplicationStatusRepository applicationStatusRepository;
    private final JobPostingStatusRepository jobPostingStatusRepository;
    private final SkillRepository skillRepository;

    @GetMapping("/application-statuses")
    public ResponseEntity<List<StatusDto>> getApplicationStatuses() {
        List<StatusDto> statuses = applicationStatusRepository.findAll().stream()
                .map(status -> new StatusDto(status.getId(), status.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/job-posting-statuses")
    public ResponseEntity<List<StatusDto>> getJobPostingStatuses() {
        List<StatusDto> statuses = jobPostingStatusRepository.findAll().stream()
                .map(status -> new StatusDto(status.getId(), status.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/skills")
    public ResponseEntity<List<SkillDto>> getSkills() {
        List<SkillDto> skills = skillRepository.findAll().stream()
                .map(skill -> new SkillDto(skill.getId(), skill.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/employment-types")
    public ResponseEntity<List<EmploymentType>> getEmploymentTypes() {
        return ResponseEntity.ok(Arrays.asList(EmploymentType.values()));
    }

    @GetMapping("/experience-levels")
    public ResponseEntity<List<ExperienceLevel>> getExperienceLevels() {
        return ResponseEntity.ok(Arrays.asList(ExperienceLevel.values()));
    }
} 