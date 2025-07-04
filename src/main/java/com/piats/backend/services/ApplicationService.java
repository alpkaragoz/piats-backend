package com.piats.backend.services;

import com.piats.backend.dto.ApplicationRequestDto;
import com.piats.backend.dto.ApplicationResponseDto;
import com.piats.backend.dto.DetailedApplicationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ApplicationService {
    ApplicationResponseDto createApplication(ApplicationRequestDto requestDto);
    DetailedApplicationResponseDto getApplicationById(UUID id);
    Page<DetailedApplicationResponseDto> getAllApplications(Integer statusId, Integer skillId, Pageable pageable);
    Page<DetailedApplicationResponseDto> getApplicationsByJobPostingId(UUID jobPostId, Pageable pageable);
    DetailedApplicationResponseDto updateApplicationStatus(UUID applicationId, Integer statusId);
} 