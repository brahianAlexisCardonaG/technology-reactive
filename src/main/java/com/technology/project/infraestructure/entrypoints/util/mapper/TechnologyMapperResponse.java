package com.technology.project.infraestructure.entrypoints.util.mapper;

import com.technology.project.domain.model.Technology;
import com.technology.project.infraestructure.entrypoints.util.response.TechnologyResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TechnologyMapperResponse {
    TechnologyResponse toTechnologyResponse(Technology technology);
}
