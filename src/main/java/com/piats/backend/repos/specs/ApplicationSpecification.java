package com.piats.backend.repos.specs;

import com.piats.backend.models.Application;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ApplicationSpecification {

    public Specification<Application> hasStatus(Integer statusId) {
        return (root, query, criteriaBuilder) -> {
            if (statusId == null) {
                return criteriaBuilder.conjunction(); // Always true, no filter applied
            }
            return criteriaBuilder.equal(root.get("status").get("id"), statusId);
        };
    }

    public Specification<Application> hasSkill(Integer skillId) {
        return (root, query, criteriaBuilder) -> {
            if (skillId == null) {
                return criteriaBuilder.conjunction(); // Always true, no filter applied
            }
            // Join Application -> ApplicationSkill -> Skill
            query.distinct(true);
            return criteriaBuilder.equal(root.join("skills").get("skill").get("id"), skillId);
        };
    }

    public Specification<Application> hasJobPostingId(UUID jobPostId) {
        return (root, query, criteriaBuilder) -> {
            if (jobPostId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("jobPosting").get("id"), jobPostId);
        };
    }

    public Specification<Application> isNotDraft() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("status").get("name"), "Draft");
    }
} 