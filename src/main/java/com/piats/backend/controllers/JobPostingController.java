package com.piats.backend.controllers;

import com.piats.backend.dto.ApplicationSummaryResponseDto;
import com.piats.backend.dto.DetailedApplicationResponseDto;
import com.piats.backend.dto.JobPostingDto;
import com.piats.backend.dto.UpdateJobPostingStatusDto;
import com.piats.backend.services.ApplicationService;
import com.piats.backend.services.JobPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/job-postings")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;
    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<JobPostingDto.JobPostingResponse> createJobPosting(@RequestBody JobPostingDto.JobPostingRequest request /* @RequestHeader("Authorization") String header */) {
        JobPostingDto.JobPostingResponse response = jobPostingService.createJobPosting(request, "todo");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostingDto.JobPostingResponse> getJobPostingById(@PathVariable UUID id) {
        JobPostingDto.JobPostingResponse response = jobPostingService.getJobPostingById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<JobPostingDto.JobPostingResponse>> getAllJobPostings(@RequestParam(required = false) String keyword) {
        List<JobPostingDto.JobPostingResponse> responses = jobPostingService.getAllJobPostings(keyword);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobPostingDto.JobPostingResponse> updateJobPosting(@PathVariable UUID id, @RequestBody JobPostingDto.JobPostingRequest request /* @RequestHeader("Authorization") String header */) {
        JobPostingDto.JobPostingResponse response = jobPostingService.updateJobPosting(id, request, "todo");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobPosting(@PathVariable UUID id /* @RequestHeader("Authorization") String header */) {
        jobPostingService.deleteJobPosting(id, "todo");
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<JobPostingDto.JobPostingResponse> updateJobPostingStatus(
            @PathVariable UUID id,
            @RequestBody UpdateJobPostingStatusDto statusDto) {
        JobPostingDto.JobPostingResponse response = jobPostingService.updateJobPostingStatus(id, statusDto.getStatusId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{jobPostId}/applications")
    public ResponseEntity<List<ApplicationSummaryResponseDto>> getApplicationsForJobPosting(
            @PathVariable UUID jobPostId) {
        List<ApplicationSummaryResponseDto> responses = applicationService.getApplicationsByJobPostingId(jobPostId);
        return ResponseEntity.ok(responses);
    }
} 