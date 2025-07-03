package com.piats.backend.repos;

import com.piats.backend.models.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobPostingRepository extends JpaRepository<JobPosting, UUID> { }
