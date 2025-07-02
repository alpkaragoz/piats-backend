package com.piats.backend.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "applicants")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Applicant {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(length = 256)
    private String firstName;

    @Column(length = 256)
    private String lastName;

    @Column(length = 256, unique = true)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String professionalSummary;

    @Column(length = 50)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String country;

    @Column(length = 20)
    private String postalCode;

    @Column(name = "linked_in_url", length = 2048)
    private String linkedInUrl;

    @Column(name = "portfolio_url", length = 2048)
    private String portfolioUrl;
}
