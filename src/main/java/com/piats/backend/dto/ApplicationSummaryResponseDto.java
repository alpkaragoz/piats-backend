package com.piats.backend.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class ApplicationSummaryResponseDto {

    private UUID applicationId;
    private String applicantName;
    private String applicantEmail;
    private String applicantPhone;
    private String professionalSummary;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private String linkedInUrl;
    private String portfolioUrl;
    private String status;
    private Integer ranking;
    private ZonedDateTime appliedAt;

} 