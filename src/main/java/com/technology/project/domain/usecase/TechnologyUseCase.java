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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class TechnologyUseCase implements TechnologyServicePort, TechnologyCapabilityServicePort {

    private final TechnologyPersistencePort technologyPersistencePort;
    private final ValidationTechnologyCapacity validationTechnologyCapacity;

    @Override
    public Mono<Technology> save(Technology technology) {

        return technologyPersistencePort.existByName(technology.getName())
                .flatMap(exists -> validationExist(exists, TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS))
                .then(Mono.defer(()->validationTechnologyCapacity.validateLengthWords(technology)))
                .then(Mono.defer(()->technologyPersistencePort.save(technology)));
    }

    @Override
    public Mono<Void> saveCapabilityTechnologies(Long capabilityId, List<Long> technologyIds) {
        return Flux.fromIterable(technologyIds)
                .flatMap(id -> technologyPersistencePort.existsById(id)
                        .flatMap(exists -> validationExist(!exists, TechnicalMessage.TECHNOLOGY_NOT_EXISTS)
                                .thenReturn(id))
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
                .flatMap(exists -> validationExist(!exists, TechnicalMessage.CAPABILITIES_NOT_EXISTS))
                .then(Mono.defer(()->technologyPersistencePort.findTechnologiesListByCapability(capabilityId)));
    }

    @Override
    public Mono<List<Technology>> getTechnologiesByIds(List<Long> technologyIds) {
        return technologyPersistencePort.findByIds(technologyIds)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_NOT_EXISTS)));
    }

    @Override
    public Mono<Void> deleteCapabilityTechnologies(List<Long> technologyIds) {
        return technologyPersistencePort.findCapabilitiesByTechnologiesIds(technologyIds)
                .map(HashSet::new)
                .flatMap(capabilityIds ->
                        validationExist(capabilityIds.size() > 1, TechnicalMessage.CAPABILITIES_TECHNOLOGIES_MORE_ONE_RELATE)
                                .then(Mono.defer(() -> technologyPersistencePort.deleteCapabilitiesTechnologies(technologyIds)))
                                .then(Mono.defer(() -> technologyPersistencePort.deleteTechnologies(technologyIds)))
                );
    }

    private Mono<Void> validationExist(Boolean condition, TechnicalMessage technicalMessage) {
        if (condition) {
            return Mono.error(new BusinessException(technicalMessage));
        }
        return Mono.empty();
    }

}