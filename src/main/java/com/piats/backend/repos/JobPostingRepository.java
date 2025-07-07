package com.piats.backend.repos;

import com.piats.backend.models.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.UUID;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, UUID> {
    // Finds JobPostings where the title contains the given keyword (case-insensitive)
    List<JobPosting> findByTitleContainingIgnoreCase(String keyword);
} 