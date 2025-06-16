package com.technology.project.infraestructure.entrypoints.technologycapability.dto;

import lombok.Data;

import java.util.List;

@Data
public class CapabilityTechnologyDto {
    private Long capabilityId;
    private List<Long> technologyIds;
}