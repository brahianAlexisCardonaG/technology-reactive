package com.technology.project.infraestructure.entrypoints.util.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.technology.project.infraestructure.entrypoints.technology.dto.TechnologyDto;
import com.technology.project.infraestructure.entrypoints.util.error.ErrorDto;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ApiResponseBase {
    private String code;
    private String message;
    private String date;
    private List<TechnologyResponse> data;
    private List<ErrorDto> errors;
}
