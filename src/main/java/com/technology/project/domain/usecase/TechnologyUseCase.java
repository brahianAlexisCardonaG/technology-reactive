package com.technology.project.domain.usecase;

import com.technology.project.domain.api.TechnologyCapabilityServicePort;
import com.technology.project.domain.api.TechnologyServicePort;
import com.technology.project.domain.spi.TechnologyPersistencePort;
import com.technology.project.domain.enums.TechnicalMessage;
import com.technology.project.domain.exception.BusinessException;
import com.technology.project.domain.model.Technology;
import com.technology.project.domain.usecase.util.ValidationTechnologyCapacity;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TechnologyUseCase implements TechnologyServicePort, TechnologyCapabilityServicePort {

    private final TechnologyPersistencePort technologyPersistencePort;
    private final ValidationTechnologyCapacity validationTechnologyCapacity;

    @Override
    public Flux<Technology> save(Flux<Technology> technology) {

        return technology
                .distinct(Technology::getName)
                    .flatMap(tech ->
                        technologyPersistencePort.findByName(tech.getName())
                                .filter(exists -> !exists)
                                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS)))
                                .flatMap(ignored ->
                                        technologyPersistencePort.save(Flux.just(tech))
                                                .next()
                                )
                                .flux()
                );
    }

    @Override
    public Mono<Void> saveCapabilityTechnologies(Long capabilityId, List<Long> technologyIds) {
        return Flux.fromIterable(technologyIds)
                .flatMap(id -> technologyPersistencePort.existsById(id)
                        .flatMap(exists -> {
                            if (!exists) {
                                return Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_NOT_EXISTS));
                            }
                            return Mono.just(id);
                        })
                )
                .collectList()
                .flatMap(validTechnologyIds ->

                        // Paso 2: Obtener tecnologías ya asociadas a la capacidad
                        technologyPersistencePort.findTechnologiesByCapability(capabilityId)
                                .flatMap(existingTechnologies -> {
                                    Set<Long> existingSet = new HashSet<>(existingTechnologies);

                                    // Paso 3: Validar duplicados y número total
                                    return validationTechnologyCapacity
                                            .validateHasDuplicatesTechnologies(existingSet, validTechnologyIds)
                                            .then(validationTechnologyCapacity.validateNumberTechnologies(existingTechnologies, validTechnologyIds))
                                            .then(technologyPersistencePort.saveRelations(capabilityId, validTechnologyIds));
                                })
                );
    }

    @Override
    public Mono<List<Technology>> findTechnologiesByCapability(Long capabilityId) {
        return technologyPersistencePort.existsCapabilityById(capabilityId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new BusinessException(TechnicalMessage.CAPABILITIES_NOT_EXISTS));
                    }
                    return technologyPersistencePort.findTechnologiesListByCapability(capabilityId);
                });
    }

    @Override
    public Flux<Technology> getTechnologiesByIds(List<Long> technologyIds) {
        return technologyPersistencePort.findByIds(technologyIds)
                .defaultIfEmpty(new Technology())
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_NOT_EXISTS)));
    }

    @Override
    public Mono<Void> deleteCapabilityTechnologies(List<Long> technologyIds) {
        return technologyPersistencePort.findCapabilitiesByTechnologiesIds(technologyIds)
                .collect(Collectors.toSet())
                .flatMap(capabilityIds -> {
                    if (capabilityIds.size() > 1) {
                        return Mono.error(new BusinessException(TechnicalMessage.CAPABILITIES_TECHNOLOGIES_MORE_ONE_RELATE));
                    }
                    return technologyPersistencePort.deleteCapabilitiesTechnologies(technologyIds)
                            .then(technologyPersistencePort.deleteTechnologies(technologyIds));
                });
    }

}