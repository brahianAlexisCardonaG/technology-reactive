package com.technology.project.infraestructure.adapters.persistenceadapter;

import com.technology.project.domain.spi.TechnologyPersistencePort;
import com.technology.project.domain.model.Technology;
import com.technology.project.infraestructure.adapters.persistenceadapter.entity.CapabilityTechnologyEntity;
import com.technology.project.infraestructure.adapters.persistenceadapter.mapper.TechnologyEntityMapper;
import com.technology.project.infraestructure.adapters.persistenceadapter.repository.CapabilityTechnologyRepository;
import com.technology.project.infraestructure.adapters.persistenceadapter.repository.TechnologyRespository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
public class TechnologyPersistenceAdapter implements TechnologyPersistencePort {
    private final TechnologyRespository technologyRespository;
    private final TechnologyEntityMapper technologyEntityMapper;
    private final CapabilityTechnologyRepository capabilityTechnologyRepository;

    @Override
    public Mono<Boolean> findByName(String name) {
        return technologyRespository.findByName(name)
                .map(technologyEntityMapper::toModel)
                .map(tech -> true)  // Si encuentra el usuario, devuelve true
                .defaultIfEmpty(false);  // Si no encuentra, devuelve false
    }

    @Override
    public Mono<Boolean> existsById(Long technologyId) {
        return technologyRespository.findById(technologyId)
                .map(technologyEntityMapper::toModel)
                .map(tech -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<List<Long>> findTechnologiesByCapability(Long capabilityId) {
        return capabilityTechnologyRepository.findByIdCapability(capabilityId)
                .map(CapabilityTechnologyEntity::getIdTechnology)
                .collectList();
    }

    @Override
    public Mono<Void> saveRelations(Long capabilityId, List<Long> technologyIds) {
        return Flux.fromIterable(technologyIds)
                .map(techId -> new CapabilityTechnologyEntity(null, capabilityId, techId))
                .collectList()
                .flatMapMany(capabilityTechnologyRepository::saveAll)
                .then();
    }

    @Override
    public Mono<List<Technology>> findTechnologiesListByCapability(Long capabilityId) {
        return capabilityTechnologyRepository.findByIdCapability(capabilityId)
                .map(CapabilityTechnologyEntity::getIdTechnology)
                .collectList()
                .flatMap(techIds ->
                        technologyRespository.findAllById(techIds)
                                .map(technologyEntityMapper::toModel)
                                .collectList()
                );
    }

    @Override
    public Mono<Boolean> existsCapabilityById(Long capabilityId) {
        return capabilityTechnologyRepository.findByIdCapability(capabilityId)
                .hasElements();
    }

    @Override
    public Flux<Technology> findByIds(List<Long> technologyIds) {
        return technologyRespository.findAllById(technologyIds)
                .map(technologyEntityMapper::toModel);
    }

    @Override
    public Flux<Long> findCapabilitiesByTechnologiesIds(List<Long> technologyIds) {
        return capabilityTechnologyRepository.findCapabilityIdsByTechnologyIds(technologyIds);
    }

    @Override
    public Mono<Void> deleteCapabilitiesTechnologies(List<Long> technologyIds) {
        return capabilityTechnologyRepository.findAll()
                .filter(entity -> technologyIds.contains(entity.getIdTechnology()))
                .collectList()
                .flatMapMany(capabilityTechnologyRepository::deleteAll)
                .then();
    }

    @Override
    public Mono<Void> deleteTechnologies(List<Long> technologyIds) {
        return technologyRespository.findAllById(technologyIds)
                .collectList()
                .flatMapMany(technologyRespository::deleteAll)
                .then();
    }


    @Override
    public Flux<Technology> save(Flux<Technology> technology) {
        return technology
                .map(technologyEntityMapper::toEntity)
                .collectList()
                .flatMapMany(technologyRespository::saveAll)
                .map(technologyEntityMapper::toModel);
    }
}
