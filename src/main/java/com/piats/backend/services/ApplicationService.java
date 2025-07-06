package com.piats.backend.services;

import com.piats.backend.dto.ApplicationRequestDto;
import com.piats.backend.dto.ApplicationSummaryResponseDto;
import com.piats.backend.dto.DetailedApplicationResponseDto;
import com.piats.backend.dto.InitiateApplicationRequestDto;
import com.piats.backend.dto.InitiateApplicationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {
    InitiateApplicationResponseDto initiateApplication(InitiateApplicationRequestDto requestDto);
    DetailedApplicationResponseDto completeApplication(UUID applicationId, ApplicationRequestDto requestDto);
    DetailedApplicationResponseDto getApplicationById(UUID id);
    Page<DetailedApplicationResponseDto> getAllApplications(Integer statusId, Integer skillId, Pageable pageable);
    List<ApplicationSummaryResponseDto> getApplicationsByJobPostingId(UUID jobPostId);
    DetailedApplicationResponseDto updateApplicationStatus(UUID applicationId, Integer statusId);
} 