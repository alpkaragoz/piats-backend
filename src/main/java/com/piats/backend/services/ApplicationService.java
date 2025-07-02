package com.piats.backend.services;

import com.piats.backend.dto.ApplicationRequestDto;
import com.piats.backend.dto.ApplicationResponseDto;

public interface ApplicationService {
    ApplicationResponseDto createApplication(ApplicationRequestDto requestDto);
} 