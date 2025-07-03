package com.piats.backend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class JobPostingRequest {
    private String title;
    private String description;
    private Integer statusId;
    private Long assigneeId;
}
