package com.piats.backend.repos.specs;

import com.piats.backend.models.JobPosting;
import org.springframework.data.jpa.domain.Specification;

public class JobPostingSpecification {

    public static Specification<JobPosting> hasTitle(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction(); // Always true, no filter
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
        };
    }

    public static Specification<JobPosting> hasStatus(Integer statusId) {
        return (root, query, criteriaBuilder) -> {
            if (statusId == null) {
                return criteriaBuilder.conjunction(); // Always true, no filter
            }
            return criteriaBuilder.equal(root.get("status").get("id"), statusId);
        };
    }
} 