package com.piats.backend.dto;

import com.piats.backend.enums.EmploymentType;
import com.piats.backend.enums.ExperienceLevel;
import lombok.Data;
import java.time.ZonedDateTime;
import java.util.UUID;

public class JobPostingDto {

    @Data
    public static class JobPostingRequest {
        private String title;
        private String description;
        private String location;
        private EmploymentType employmentType;
        private ExperienceLevel experienceLevel;
        private Integer statusId;
        private UUID createdById;
        private UUID assigneeId;
    }

    @Data
    public static class JobPostingResponse {
        private UUID id;
        private String title;
        private String description;
        private String location;
        private EmploymentType employmentType;
        private ExperienceLevel experienceLevel;
        private String status;
        private UserDto createdBy;
        private UserDto assignee;
        private ZonedDateTime createdAt;
    }

    @Data
    public static class UserDto {
        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
    }
} 