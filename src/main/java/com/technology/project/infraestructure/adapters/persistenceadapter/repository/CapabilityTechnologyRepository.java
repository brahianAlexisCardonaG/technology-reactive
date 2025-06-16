package com.technology.project.infraestructure.adapters.persistenceadapter.repository;

import com.technology.project.infraestructure.adapters.persistenceadapter.entity.CapabilityTechnologyEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface CapabilityTechnologyRepository extends ReactiveCrudRepository<CapabilityTechnologyEntity, Long> {

    Flux<CapabilityTechnologyEntity> findByIdCapability(Long idCapability);

    @Query("SELECT id_capability FROM capability_technology WHERE id_technology IN (:idTechnologies)")
    Flux<Long> findCapabilityIdsByTechnologyIds(List<Long> idTechnologies);

}
