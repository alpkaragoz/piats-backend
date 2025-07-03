package com.piats.backend.repos;

import com.piats.backend.models.JobPostingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostingStatusRepository extends JpaRepository<JobPostingStatus, Integer> {
} 