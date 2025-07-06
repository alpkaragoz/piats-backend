package com.piats.backend.controllers;

import com.piats.backend.dto.ApplicationRequestDto;
import com.piats.backend.dto.ApplicationResponseDto;
import com.piats.backend.dto.DetailedApplicationResponseDto;
import com.piats.backend.dto.InitiateApplicationRequestDto;
import com.piats.backend.dto.InitiateApplicationResponseDto;
import com.piats.backend.dto.UpdateApplicationStatusDto;
import com.piats.backend.services.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/initiate")
    public ResponseEntity<InitiateApplicationResponseDto> initiateApplication(@RequestBody InitiateApplicationRequestDto requestDto) {
        InitiateApplicationResponseDto response = applicationService.initiateApplication(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailedApplicationResponseDto> getApplicationById(@PathVariable java.util.UUID id) {
        DetailedApplicationResponseDto response = applicationService.getApplicationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DetailedApplicationResponseDto>> getAllApplications(
            @RequestParam(required = false) Integer statusId,
            @RequestParam(required = false) Integer skillId) {
        List<DetailedApplicationResponseDto> responses = applicationService.getAllApplications(statusId, skillId);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DetailedApplicationResponseDto> updateApplicationStatus(
            @PathVariable java.util.UUID id,
            @RequestBody UpdateApplicationStatusDto statusDto) {
        DetailedApplicationResponseDto response = applicationService.updateApplicationStatus(id, statusDto.getStatusId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetailedApplicationResponseDto> completeApplication(
            @PathVariable UUID id,
            @RequestBody ApplicationRequestDto requestDto) {
        DetailedApplicationResponseDto response = applicationService.completeApplication(id, requestDto);
        return ResponseEntity.ok(response);
    }
} 