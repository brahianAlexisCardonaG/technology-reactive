package com.technology.project.infraestructure.entrypoints.util.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TechnologyResponse {
    private Long id;
    private String name;
    private String description;
}
