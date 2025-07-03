package com.piats.backend.repos.specs;

import com.piats.backend.models.Application;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

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
} 