package com.piats.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Confession {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @Column(name = "confession_text", length = 256, nullable = false)
    private String confessionText;

    @Column(name = "department")
    private String department;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;
}
