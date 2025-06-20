package com.technology.project.infraestructure.entrypoints.technology.validations;

import com.technology.project.domain.enums.TechnicalMessage;
import com.technology.project.domain.exception.BusinessException;
import com.technology.project.infraestructure.entrypoints.technology.dto.TechnologyDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TechnologyValidationDto {

    public Mono<Void> validateFieldNotNullOrBlank(TechnologyDto dto) {
        if (dto.getDescription() == null || dto.getName() == null ) {
            return Mono.error(new BusinessException(TechnicalMessage.INVALID_PARAMETERS));
        }
        return Mono.empty();
    }

    public Mono<Void> validateNoDuplicateNames(List<TechnologyDto> dtoList) {
        Set<String> names = new HashSet<>();
        List<String> duplicatedNames = dtoList.stream()
                .map(TechnologyDto::getName)
                .filter(name -> !names.add(name)) // Si no se puede agregar al set, es duplicado
                .toList();

        if (!duplicatedNames.isEmpty()) {
            return Mono.error(new BusinessException(TechnicalMessage.DUPLICATE_NAMES_TECHNOLOGIES));
        }

        return Mono.empty();
    }

}
