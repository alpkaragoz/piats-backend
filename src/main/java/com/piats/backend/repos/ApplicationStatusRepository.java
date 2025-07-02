package com.piats.backend.repos;

import com.piats.backend.models.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationStatusRepository extends JpaRepository<ApplicationStatus, Integer> {
} 