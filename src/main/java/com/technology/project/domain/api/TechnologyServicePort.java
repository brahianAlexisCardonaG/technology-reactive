package com.technology.project.domain.api;

import com.technology.project.domain.model.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyServicePort {
    Flux<Technology> save(Flux<Technology> tecnologic);
    Flux<Technology> getTechnologiesByIds(List<Long> technologyIds);
}
