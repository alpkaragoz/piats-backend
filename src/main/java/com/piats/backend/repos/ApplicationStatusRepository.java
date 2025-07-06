package com.piats.backend.repos;

import com.piats.backend.models.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationStatusRepository extends JpaRepository<ApplicationStatus, Integer> {
    Optional<ApplicationStatus> findByName(String name);
} 