package com.technology.project.infraestructure.entrypoints.technology.handler;

import com.technology.project.domain.api.TechnologyServicePort;
import com.technology.project.domain.enums.TechnicalMessage;
import com.technology.project.infraestructure.entrypoints.util.mapper.TechnologyMapper;
import com.technology.project.infraestructure.entrypoints.technology.dto.TechnologyDto;
import com.technology.project.infraestructure.entrypoints.util.error.ApplyErrorHandler;
import com.technology.project.infraestructure.entrypoints.util.mapper.TechnologyMapperResponse;
import com.technology.project.infraestructure.entrypoints.util.response.ApiResponseBase;
import com.technology.project.infraestructure.entrypoints.technology.validations.TechnologyValidationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.technology.project.infraestructure.entrypoints.util.Constants.TECHNOLOGY_ERROR;
import static com.technology.project.infraestructure.entrypoints.util.Constants.X_MESSAGE_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TechnologyHandlerImpl {

    private final TechnologyServicePort technologyServicePort;
    private final TechnologyMapper technologyMapper;
    private final TechnologyValidationDto technologyValidationDto;
    private final ApplyErrorHandler applyErrorHandler;
    private final TechnologyMapperResponse technologyMapperResponse;

    public Mono<ServerResponse> create(ServerRequest request) {

        Mono<ServerResponse> response = request.bodyToFlux(TechnologyDto.class)
                .collectList()
                .flatMap(technologyValidationDto::validateNoDuplicateNames)
                .flatMapMany(Flux::fromIterable)
                .flatMap(technologyValidationDto::validateFieldNotNullOrBlank)
                .flatMap(technologyValidationDto::validateLengthWords)
                .map(technologyMapper::toTechnology)
                .transform(technologyServicePort::save)
                .collectList()
                .map(savedTechList -> savedTechList.stream()
                        .map(technologyMapperResponse::toTechnologyResponse)
                        .collect(Collectors.toList()))
                .flatMap(savedTechList ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiResponseBase.builder()
                                        .code(TechnicalMessage.TECHNOLOGY_CREATED.getMessage())
                                        .message(TechnicalMessage.TECHNOLOGY_CREATED.getMessage())
                                        .date(Instant.now().toString())
                                        .data(savedTechList)
                                        .build()
                                )
                )
                .contextWrite(Context.of(X_MESSAGE_ID, ""))
                .doOnError(ex -> log.error(TECHNOLOGY_ERROR, ex));

        return applyErrorHandler.applyErrorHandling(response);

    }

    public Mono<ServerResponse> getTechnologiesByIds(ServerRequest request) {
        List<Long> technologyIds = request.queryParams()
                .get("ids")
                .stream()
                .flatMap(param -> Arrays.stream(param.split(",")))
                .map(Long::parseLong)
                .toList();

        Mono<ServerResponse> response = technologyServicePort.getTechnologiesByIds(technologyIds)
                .map(technologyMapperResponse::toTechnologyResponse)
                .collectList()
                .flatMap(techList -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponseBase.builder()
                                .code(TechnicalMessage.TECHNOLOGIES_FOUND.getCode())
                                .message(TechnicalMessage.TECHNOLOGIES_FOUND.getMessage())
                                .date(Instant.now().toString())
                                .data(techList)
                                .build()
                        )
                )
                .contextWrite(Context.of(X_MESSAGE_ID, ""))
                .doOnError(ex -> log.error(TECHNOLOGY_ERROR, ex));

        return applyErrorHandler.applyErrorHandling(response);
    }
}