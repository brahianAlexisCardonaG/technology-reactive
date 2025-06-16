package com.technology.project.infraestructure.entrypoints.technologycapability.handler;

import com.technology.project.domain.api.TechnologyCapabilityServicePort;
import com.technology.project.domain.enums.TechnicalMessage;
import com.technology.project.infraestructure.entrypoints.technologycapability.dto.CapabilityTechnologyDto;
import com.technology.project.infraestructure.entrypoints.util.mapper.TechnologyMapper;
import com.technology.project.infraestructure.entrypoints.util.mapper.TechnologyMapperResponse;
import com.technology.project.infraestructure.entrypoints.util.response.ApiResponseBase;
import com.technology.project.infraestructure.entrypoints.util.response.ApiResponseBaseMap;
import com.technology.project.infraestructure.entrypoints.technologycapability.validations.CapabilityTechnologyValidationDto;
import com.technology.project.infraestructure.entrypoints.util.error.ApplyErrorHandler;
import com.technology.project.infraestructure.entrypoints.util.response.TechnologyResponse;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.technology.project.infraestructure.entrypoints.util.Constants.TECHNOLOGY_ERROR;
import static com.technology.project.infraestructure.entrypoints.util.Constants.X_MESSAGE_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TechnologyCapabilityHandlerImpl {

    private final CapabilityTechnologyValidationDto capabilityTechnologyValidationDto;
    private final TechnologyCapabilityServicePort technologyCapabilityServicePort;
    private final ApplyErrorHandler applyErrorHandler;
    private final TechnologyMapperResponse technologyMapperResponse;

    public Mono<ServerResponse> createCapabilityTechnologies(ServerRequest request) {

        Mono<ServerResponse> response = request.bodyToMono(CapabilityTechnologyDto.class)
                .flatMap(capabilityTechnologyValidationDto::validateDuplicateIds)
                .flatMap(capabilityTechnologyValidationDto::validateFieldNotNullOrBlank)
                .flatMap(dto -> technologyCapabilityServicePort.
                        saveCapabilityTechnologies(dto.getCapabilityId(), dto.getTechnologyIds()))
                .then(ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponseBase.builder()
                                .code(TechnicalMessage.TECHNOLOGY_CAPABILITY_CREATED.getCode())
                                .message(TechnicalMessage.TECHNOLOGY_CAPABILITY_CREATED.getMessage())
                                .date(Instant.now().toString())
                                .build()
                        )
                )
                .contextWrite(Context.of(X_MESSAGE_ID, ""))
                .doOnError(ex -> log.error(TECHNOLOGY_ERROR, ex));

        return applyErrorHandler.applyErrorHandling(response);
    }

    public Mono<ServerResponse> deleteCapabilitiesTechnologies(ServerRequest request) {
        List<Long> technologyIds = request.queryParams()
                .get("technologyIds")
                .stream()
                .flatMap(param -> Arrays.stream(param.split(",")))
                .map(Long::parseLong)
                .toList();

        Mono<ServerResponse> response = technologyCapabilityServicePort.deleteCapabilityTechnologies(technologyIds)
                .then(ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponseBase.builder()
                                .code(TechnicalMessage.CAPABILITIES_TECHNOLOGIES_DELETE.getCode())
                                .message(TechnicalMessage.CAPABILITIES_TECHNOLOGIES_DELETE.getMessage())
                                .date(Instant.now().toString())
                                .build()
                        )
                )
                .contextWrite(Context.of(X_MESSAGE_ID, ""))
                .doOnError(ex -> log.error(TECHNOLOGY_ERROR, ex));

        return applyErrorHandler.applyErrorHandling(response);
    }

    public Mono<ServerResponse> getTechnologiesListByCapabilityIds(ServerRequest request) {

        List<Long> capabilityIds = request.queryParams()
                .get("capabilityIds")
                .stream()
                .flatMap(param -> Arrays.stream(param.split(",")))
                .map(Long::parseLong)
                .toList();

        Mono<ServerResponse> response = Flux.fromIterable(capabilityIds)
                .flatMap(capabilityId ->
                        technologyCapabilityServicePort.findTechnologiesByCapability(capabilityId)
                                .map(techList -> {
                                    List<TechnologyResponse> dtoList = techList.stream()
                                            .map(technologyMapperResponse::toTechnologyResponse)
                                            .toList();
                                    return Map.entry(capabilityId, dtoList);
                                })
                )
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .flatMap(resultMap ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiResponseBaseMap.builder()
                                        .code(TechnicalMessage.CAPABILITIES_TECHNOLOGY_FOUND.getCode())
                                        .message(TechnicalMessage.CAPABILITIES_TECHNOLOGY_FOUND.getMessage())
                                        .date(Instant.now().toString())
                                        .data(resultMap) // este data ahora es un Map<Long, List<TechnologyDto>>
                                        .build())
                )
                .contextWrite(Context.of(X_MESSAGE_ID, ""))
                .doOnError(ex -> log.error(TECHNOLOGY_ERROR, ex));

        return applyErrorHandler.applyErrorHandling(response);
    }
}
