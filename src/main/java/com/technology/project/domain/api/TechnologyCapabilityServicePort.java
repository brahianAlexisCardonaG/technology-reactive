package com.technology.project.domain.api;

import com.technology.project.domain.model.Technology;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyCapabilityServicePort {
    Mono<Void> deleteCapabilityTechnologies(List<Long> technologyIds);
    Mono<Void> saveCapabilityTechnologies(Long capabilityId, List<Long> technologyIds);
    Mono<List<Technology>> findTechnologiesByCapability(Long capabilityId);
}
