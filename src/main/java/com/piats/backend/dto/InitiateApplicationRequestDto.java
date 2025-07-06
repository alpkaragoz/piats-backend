package com.piats.backend.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class InitiateApplicationRequestDto {
    private UUID jobPostId;
} 