package com.technology.project.domain.spi;

import com.technology.project.domain.model.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyPersistencePort {
    Mono<Technology> save(Technology technology);
    Mono<Boolean> existByName(String name);
    Mono<Boolean> existsById(Long technologyId);
    Mono<List<Long>> findTechnologiesByCapability(Long capabilityId);
    Mono<Void> saveRelations(Long capabilityId, List<Long> technologyIds);
    Mono<List<Technology>> findTechnologiesListByCapability(Long capabilityId);
    Mono<Boolean> existsCapabilityById(Long capabilityId);
    Mono<List<Technology>> findByIds(List<Long> technologyIds);
    Mono<List<Long>> findCapabilitiesByTechnologiesIds(List<Long> technologyIds);
    Mono<Void> deleteCapabilitiesTechnologies(List<Long> technologyIds);
    Mono<Void> deleteTechnologies(List<Long> technologyIds);
}