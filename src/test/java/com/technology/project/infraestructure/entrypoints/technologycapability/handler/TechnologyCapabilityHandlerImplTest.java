package com.technology.project.infraestructure.entrypoints.technologycapability.handler;

import com.technology.project.domain.api.TechnologyCapabilityServicePort;
import com.technology.project.domain.enums.TechnicalMessage;
import com.technology.project.domain.model.Technology;
import com.technology.project.infraestructure.entrypoints.technologycapability.dto.CapabilityTechnologyDto;
import com.technology.project.infraestructure.entrypoints.technologycapability.validations.CapabilityTechnologyValidationDto;
import com.technology.project.infraestructure.entrypoints.util.Constants;
import com.technology.project.infraestructure.entrypoints.util.error.ApplyErrorHandler;
import com.technology.project.infraestructure.entrypoints.util.mapper.TechnologyMapper;
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
import reactor.core.publisher.Mono;

import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TechnologyCapabilityHandlerImplTest {

    @Mock
    private CapabilityTechnologyValidationDto capabilityValidationDto;
    @Mock
    private TechnologyCapabilityServicePort servicePort;
    @Mock
    private ApplyErrorHandler applyErrorHandler;
    @Mock
    private TechnologyMapperResponse technologyMapperResponse;

    private TechnologyCapabilityHandlerImpl handler;
    private WebTestClient webClient;

    @BeforeEach
    void setUp() {
        handler = new TechnologyCapabilityHandlerImpl(
                capabilityValidationDto,
                servicePort,
                applyErrorHandler,
                technologyMapperResponse
        );

        var router = RouterFunctions
                .route(RequestPredicates.POST(Constants.PATH_POST_TECHNOLOGY_CAPABILITY), handler::createCapabilityTechnologies)
                .andRoute(RequestPredicates.GET(Constants.PATH_GET_TECHNOLOGIES_BY_IDS_CAPABILITIES), handler::getTechnologiesListByCapabilityIds)
                .andRoute(RequestPredicates.DELETE(Constants.PATH_DELETE_RELATE_CAPABILITIES_TECHNOLOGIES), handler::deleteCapabilitiesTechnologies);

        webClient = WebTestClient.bindToRouterFunction(router).build();
    }

    @Test
    void createCapabilityTechnologies_shouldReturnCreated() {
        CapabilityTechnologyDto dto = new CapabilityTechnologyDto();
        dto.setCapabilityId(1L);
        dto.setTechnologyIds(List.of(1L,2L));

        when(capabilityValidationDto.validateDuplicateIds(any()))
                .thenReturn(Mono.just(dto));
        when(capabilityValidationDto.validateFieldNotNullOrBlank(any()))
                .thenReturn(Mono.just(dto));
        when(servicePort.saveCapabilityTechnologies(1L, List.of(1L,2L)))
                .thenReturn(Mono.empty());
        when(applyErrorHandler.applyErrorHandling(any(Mono.class)))
                .thenAnswer(i -> i.getArgument(0));

        webClient.post()
                .uri(Constants.PATH_POST_TECHNOLOGY_CAPABILITY)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void getTechnologiesListByCapabilityIds_shouldReturnOkWithData() {
        // Two capability IDs: 1 and 2
        List<Technology> techList = List.of(new Technology(101L, "Java", "desc"));
        when(servicePort.findTechnologiesByCapability(1L))
                .thenReturn(Mono.just(techList));
        when(servicePort.findTechnologiesByCapability(2L))
                .thenReturn(Mono.just(techList));
        TechnologyResponse respDto = new TechnologyResponse();
        respDto.setId(101L);
        respDto.setName("Java");
        respDto.setDescription("desc");
        when(technologyMapperResponse.toTechnologyResponse(any()))
                .thenReturn(respDto);
        when(applyErrorHandler.applyErrorHandling(any(Mono.class)))
                .thenAnswer(i -> i.getArgument(0));

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(Constants.PATH_GET_TECHNOLOGIES_BY_IDS_CAPABILITIES)
                        .queryParam("capabilityIds","1,2")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.CAPABILITIES_TECHNOLOGY_FOUND.getCode())
                // Verify map contains lists under keys "1" and "2"
                .jsonPath("$.data['1'][0].name").isEqualTo("Java")
                .jsonPath("$.data['2'][0].description").isEqualTo("desc");
    }

    @Test
    void deleteCapabilitiesTechnologies_shouldReturnOk() {
        when(servicePort.deleteCapabilityTechnologies(List.of(1L,2L,3L)))
                .thenReturn(Mono.empty());
        when(applyErrorHandler.applyErrorHandling(any(Mono.class)))
                .thenAnswer(i -> i.getArgument(0));

        webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(Constants.PATH_DELETE_RELATE_CAPABILITIES_TECHNOLOGIES)
                        .queryParam("technologyIds","1,2,3")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }
}
