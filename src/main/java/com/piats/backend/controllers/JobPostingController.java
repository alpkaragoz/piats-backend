package com.piats.backend.controllers;

import com.piats.backend.dto.JobPostingRequest;
import com.piats.backend.models.JobPosting;
import com.piats.backend.models.User;
import com.piats.backend.repos.JobPostingRepository;
import com.piats.backend.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.classfile.Module.Uses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/job-postings")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createJobPosting(@RequestBody JobPostingRequest request, Principal principal) {
        Optional<User> creatorOpt = userRepository.findByEmail(principal.getName()); //  retrieves the email embedded in the JWT.
                                                                                    //   Uses the email from the JWT to fetch the User entity from your database
        if (creatorOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User creator = creatorOpt.get();
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElse(null);
        }

        JobPosting posting = JobPosting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .statusId(request.getStatusId())
                .createdBy(creator)
                .assignee(assignee)
                .build();

        jobPostingRepository.save(posting);

        return ResponseEntity.ok(posting);
    }
}
