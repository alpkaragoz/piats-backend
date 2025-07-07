package com.piats.backend.repos;

import com.piats.backend.models.Confession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConfessionRepository extends JpaRepository<Confession, UUID> {
}
