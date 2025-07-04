package com.piats.backend.controllers;

import com.piats.backend.dto.ApplicationRequestDto;
import com.piats.backend.dto.ApplicationResponseDto;
import com.piats.backend.dto.DetailedApplicationResponseDto;
import com.piats.backend.dto.UpdateApplicationStatusDto;
import com.piats.backend.services.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<DetailedApplicationResponseDto> getApplicationById(@PathVariable java.util.UUID id) {
        DetailedApplicationResponseDto response = applicationService.getApplicationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<DetailedApplicationResponseDto>> getAllApplications(
            @RequestParam(required = false) Integer statusId,
            @RequestParam(required = false) Integer skillId,
            Pageable pageable) {
        Page<DetailedApplicationResponseDto> responses = applicationService.getAllApplications(statusId, skillId, pageable);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DetailedApplicationResponseDto> updateApplicationStatus(
            @PathVariable java.util.UUID id,
            @RequestBody UpdateApplicationStatusDto statusDto) {
        DetailedApplicationResponseDto response = applicationService.updateApplicationStatus(id, statusDto.getStatusId());
        return ResponseEntity.ok(response);
    }
} 