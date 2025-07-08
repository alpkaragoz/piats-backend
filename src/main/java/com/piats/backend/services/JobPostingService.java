package com.piats.backend.services;

import com.piats.backend.dto.JobPostingDto;
import java.util.List;
import java.util.UUID;

public interface JobPostingService {
    JobPostingDto.JobPostingResponse createJobPosting(JobPostingDto.JobPostingRequest request, String header);
    JobPostingDto.JobPostingResponse getJobPostingById(UUID id);
    List<JobPostingDto.JobPostingResponse> getAllJobPostings(String keyword, Integer statusId);
    JobPostingDto.JobPostingResponse updateJobPosting(UUID id, JobPostingDto.JobPostingRequest request, String header);
    void deleteJobPosting(UUID id, String header);
    JobPostingDto.JobPostingResponse updateJobPostingStatus(UUID id, Integer statusId);
} 