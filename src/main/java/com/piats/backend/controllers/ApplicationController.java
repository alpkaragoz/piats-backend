package com.piats.backend.controllers;

import com.piats.backend.dto.ApplicationRequestDto;
import com.piats.backend.dto.ApplicationResponseDto;
import com.piats.backend.services.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApplicationResponseDto> createApplication(@RequestBody ApplicationRequestDto requestDto) {
        ApplicationResponseDto response = applicationService.createApplication(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
} 