package com.technology.project.infraestructure.entrypoints.technologycapability.validations;

import com.technology.project.domain.enums.TechnicalMessage;
import com.technology.project.domain.exception.BusinessException;
import com.technology.project.infraestructure.entrypoints.technologycapability.dto.CapabilityTechnologyDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CapabilityTechnologyValidationDto {
    public Mono<CapabilityTechnologyDto> validateDuplicateIds(CapabilityTechnologyDto dto) {
        Set<Long> ids = new HashSet<>();
        List<Long> duplicatedIds = dto.getTechnologyIds().stream()
                .filter(id -> !ids.add(id)) // Si no se puede agregar al set, es duplicado
                .toList();

        if (!duplicatedIds.isEmpty()) {
            return Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGIES_DUPLICATES_IDS));
        }

        return Mono.just(dto);
    }

    public Mono<CapabilityTechnologyDto> validateFieldNotNullOrBlank(CapabilityTechnologyDto dto) {
        if (dto.getCapabilityId() == null || dto.getTechnologyIds() == null || dto.getTechnologyIds().isEmpty()) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_PARAMETERS));
        }
        return Mono.just(dto);
    }
}
