package com.technology.project.infraestructure.adapters.persistenceadapter.mapper;

import com.technology.project.domain.model.Technology;
import com.technology.project.infraestructure.adapters.persistenceadapter.entity.TechnologyEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TechnologyEntityMapper {
    Technology toModel(TechnologyEntity technologyEntity);
    TechnologyEntity toEntity(Technology technology);
}
