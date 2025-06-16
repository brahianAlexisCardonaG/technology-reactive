package com.technology.project.infraestructure.entrypoints.util.mapper;

import com.technology.project.domain.model.Technology;
import com.technology.project.infraestructure.entrypoints.technology.dto.TechnologyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TechnologyMapper {

    @Mapping(target = "id", ignore = true)
    Technology toTechnology(TechnologyDto technologyDto);

    TechnologyDto toTechnologyDto(Technology technology);
}
