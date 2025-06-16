package com.technology.project.domain.usecase.util;

import com.technology.project.domain.enums.TechnicalMessage;
import com.technology.project.domain.exception.BusinessException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
public class ValidationTechnologyCapacity {

    public Mono<Void> validateHasDuplicatesTechnologies(Set<Long> existingSet, List<Long> technologyIds) {
        boolean hasDuplicates = technologyIds.stream().anyMatch(existingSet::contains);
        if (hasDuplicates) {
            return Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_TECH_ALREADY_ASSOCIATED));
        }
        return Mono.empty();
    }

    public Mono<Void> validateNumberTechnologies(List<Long> existingTechnologies, List<Long> technologyIds) {
        int total = existingTechnologies.size() + technologyIds.size();
        if (total > 20) {
            return Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_TWENTY_ASSOCIATION));
        }
        if (total < 3) {
            return Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_THREE_ASSOCIATION));
        }
        return Mono.empty();
    }
}