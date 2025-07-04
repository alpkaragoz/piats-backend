package com.piats.backend.services;

import com.piats.backend.dto.JobPostingDto;
import com.piats.backend.exceptions.UserNotFoundException;
import com.piats.backend.models.JobPosting;
import com.piats.backend.models.JobPostingStatus;
import com.piats.backend.models.User;
import com.piats.backend.repos.JobPostingRepository;
import com.piats.backend.repos.JobPostingStatusRepository;
import com.piats.backend.repos.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final JobPostingStatusRepository jobPostingStatusRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public JobPostingDto.JobPostingResponse createJobPosting(JobPostingDto.JobPostingRequest request) {
        JobPosting jobPosting = new JobPosting();
        mapRequestToJobPosting(request, jobPosting);
        JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);
        return mapJobPostingToResponse(savedJobPosting);
    }

    @Override
    public JobPostingDto.JobPostingResponse getJobPostingById(UUID id) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("JobPosting not found with id: " + id));
        return mapJobPostingToResponse(jobPosting);
    }
    
    @Override
    public List<JobPostingDto.JobPostingResponse> getAllJobPostings() {
        return jobPostingRepository.findAll().stream()
                .map(this::mapJobPostingToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobPostingDto.JobPostingResponse updateJobPosting(UUID id, JobPostingDto.JobPostingRequest request) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("JobPosting not found with id: " + id));
        mapRequestToJobPosting(request, jobPosting);
        JobPosting updatedJobPosting = jobPostingRepository.save(jobPosting);
        return mapJobPostingToResponse(updatedJobPosting);
    }

    @Override
    public void deleteJobPosting(UUID id) {
        if (!jobPostingRepository.existsById(id)) {
            throw new EntityNotFoundException("JobPosting not found with id: " + id);
        }
        jobPostingRepository.deleteById(id);
    }
    
    private void mapRequestToJobPosting(JobPostingDto.JobPostingRequest request, JobPosting jobPosting) {
        jobPosting.setTitle(request.getTitle());
        jobPosting.setDescription(request.getDescription());
        jobPosting.setLocation(request.getLocation());
        jobPosting.setEmploymentType(request.getEmploymentType());
        jobPosting.setExperienceLevel(request.getExperienceLevel());

        if (request.getStatusId() != null) {
            JobPostingStatus status = jobPostingStatusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new EntityNotFoundException("JobPostingStatus not found with id: " + request.getStatusId()));
            jobPosting.setStatus(status);
        }

        if (request.getCreatedById() != null) {
            User createdBy = userRepository.findById(request.getCreatedById())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getCreatedById()));
            jobPosting.setCreatedBy(createdBy);
        }

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getAssigneeId()));
            jobPosting.setAssignee(assignee);
        }
    }

    private JobPostingDto.JobPostingResponse mapJobPostingToResponse(JobPosting jobPosting) {
        JobPostingDto.JobPostingResponse response = new JobPostingDto.JobPostingResponse();
        response.setId(jobPosting.getId());
        response.setTitle(jobPosting.getTitle());
        response.setDescription(jobPosting.getDescription());
        response.setLocation(jobPosting.getLocation());
        response.setEmploymentType(jobPosting.getEmploymentType());
        response.setExperienceLevel(jobPosting.getExperienceLevel());
        response.setCreatedAt(jobPosting.getCreatedAt());
        
        if (jobPosting.getStatus() != null) {
            response.setStatus(jobPosting.getStatus().getName());
        }

        if (jobPosting.getCreatedBy() != null) {
            response.setCreatedBy(mapUserToUserDto(jobPosting.getCreatedBy()));
        }
        
        if (jobPosting.getAssignee() != null) {
            response.setAssignee(mapUserToUserDto(jobPosting.getAssignee()));
        }

        return response;
    }

    private JobPostingDto.UserDto mapUserToUserDto(User user) {
        JobPostingDto.UserDto userDto = new JobPostingDto.UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
} 