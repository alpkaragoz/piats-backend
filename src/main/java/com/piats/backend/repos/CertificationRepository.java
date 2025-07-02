package com.piats.backend.repos;

import com.piats.backend.models.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, UUID> {
} 