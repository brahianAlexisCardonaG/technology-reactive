package com.technology.project.infraestructure.entrypoints.technology.handler;

import com.technology.project.domain.api.TechnologyServicePort;
import com.technology.project.domain.enums.TechnicalMessage;
import com.technology.project.domain.model.Technology;
import com.technology.project.infraestructure.entrypoints.technology.dto.TechnologyDto;
import com.technology.project.infraestructure.entrypoints.util.Constants;
import com.technology.project.infraestructure.entrypoints.util.mapper.TechnologyMapper;
import com.technology.project.infraestructure.entrypoints.util.error.ApplyErrorHandler;
import com.technology.project.infraestructure.entrypoints.technology.validations.TechnologyValidationDto;
import com.technology.project.infraestructure.entrypoints.util.mapper.TechnologyMapperResponse;
import com.technology.project.infraestructure.entrypoints.util.response.TechnologyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TechnologyHandlerImplTest {

    @Mock
    private TechnologyServicePort technologyServicePort;
    @Mock
    private TechnologyMapper technologyMapper;
    @Mock
    private TechnologyValidationDto technologyValidationDto;
    @Mock
    private ApplyErrorHandler applyErrorHandler;
    @Mock
    private TechnologyMapperResponse technologyMapperResponse;

    private TechnologyHandlerImpl handler;
    private WebTestClient webClient;

    @BeforeEach
    void setUp() {
        handler = new TechnologyHandlerImpl(
                technologyServicePort,
                technologyMapper,
                technologyValidationDto,
                applyErrorHandler,
                technologyMapperResponse
        );

        var router = RouterFunctions.route(RequestPredicates.POST(Constants.PATH_POST_TECHNOLOGY), handler::create)
                .andRoute(RequestPredicates.GET(Constants.PATH_GET_TECHNOLOGIES_BY_IDS), handler::getTechnologiesByIds);
        webClient = WebTestClient.bindToRouterFunction(router).build();
    }

    @Test
    void create_shouldReturnCreatedAndValidBody() {
        // Arrange
        TechnologyDto dto = new TechnologyDto();
        dto.setName("React");

        // Simulate validations
        when(technologyValidationDto.validateNoDuplicateNames(any()))
                .thenReturn(Mono.just(List.of(dto)));
        when(technologyValidationDto.validateFieldNotNullOrBlank(any()))
                .thenReturn(Mono.just(dto));
        when(technologyValidationDto.validateLengthWords(any()))
                .thenReturn(Mono.just(dto));

        // Map to domain
        var techDomain = new Technology();
        when(technologyMapper.toTechnology(dto))
                .thenReturn(techDomain);

        // Service saves and echoes back
        when(technologyServicePort.save(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Map to response DTO
        TechnologyResponse techResponse = new TechnologyResponse();
        techResponse.setName("React");
        when(technologyMapperResponse.toTechnologyResponse(techDomain))
                .thenReturn(techResponse);

        // Error handler is identity
        when(applyErrorHandler.applyErrorHandling(any(Mono.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act & Assert
        webClient.post()
                .uri(Constants.PATH_POST_TECHNOLOGY)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(List.of(dto)))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.TECHNOLOGY_CREATED.getMessage())
                .jsonPath("$.data").isArray()
                .jsonPath("$.data.length()").isEqualTo(1)
                .jsonPath("$.data[0].name").isEqualTo("React");
    }

    @Test
    void getTechnologiesByIds_shouldReturnOkAndValidBody() {
        // Arrange
        List<Long> ids = List.of(10L, 20L);
        var techDomain = new Technology();
        when(technologyServicePort.getTechnologiesByIds(ids))
                .thenReturn(Flux.just(techDomain));

        TechnologyResponse techResponse = new TechnologyResponse();
        techResponse.setName("Spring");
        when(technologyMapperResponse.toTechnologyResponse(techDomain))
                .thenReturn(techResponse);
        when(applyErrorHandler.applyErrorHandling(any(Mono.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act & Assert
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(Constants.PATH_GET_TECHNOLOGIES_BY_IDS)
                        .queryParam("ids", "10,20")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.TECHNOLOGIES_FOUND.getCode())
                .jsonPath("$.data").isArray()
                .jsonPath("$.data.length()").isEqualTo(1)
                .jsonPath("$.data[0].name").isEqualTo("Spring");
    }
}