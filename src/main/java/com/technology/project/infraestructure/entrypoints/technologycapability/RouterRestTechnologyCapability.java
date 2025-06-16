package com.technology.project.infraestructure.entrypoints.technologycapability;

import com.technology.project.infraestructure.entrypoints.technologycapability.dto.CapabilityTechnologyDto;
import com.technology.project.infraestructure.entrypoints.technologycapability.handler.TechnologyCapabilityHandlerImpl;
import com.technology.project.infraestructure.entrypoints.util.response.ApiResponseBase;
import com.technology.project.infraestructure.entrypoints.util.response.ApiResponseBaseMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.technology.project.infraestructure.entrypoints.util.Constants.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
@Tag(name = "Technology", description = "API Technologies")
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class RouterRestTechnologyCapability {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = PATH_POST_TECHNOLOGY_CAPABILITY,
                    produces = {"application/json"},
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = TechnologyCapabilityHandlerImpl.class,
                    beanMethod = "createCapabilityTechnologies",
                    operation = @Operation(
                            operationId = "createCapabilityTechnologies",
                            summary = "Associate technologies with a capability",
                            tags = { "Endpoints for webclients" },
                            security = @SecurityRequirement(name = "BearerAuth"),
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = CapabilityTechnologyDto.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Relation created",
                                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                                    ),
                                    @ApiResponse(responseCode = "401", description = "Unauthorized")
                            }
                    )
            ),
            @RouterOperation(
                    path = PATH_GET_TECHNOLOGIES_BY_IDS_CAPABILITIES,
                    produces = {"application/json"},
                    method = org.springframework.web.bind.annotation.RequestMethod.GET,
                    beanClass = TechnologyCapabilityHandlerImpl.class,
                    beanMethod = "getTechnologiesListByCapabilityIds",
                    operation = @Operation(
                            operationId = "getTechnologiesByCapabilities",
                            summary = "Obtain technologies by capability IDs",
                            tags = { "Endpoints for webclients" },
                            security = @SecurityRequirement(name = "BearerAuth"),
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            in = ParameterIn.QUERY,
                                            name = "capabilityIds",
                                            description = "List of ids separated by commas (,)",
                                            example = "1,2,3",
                                            required = true
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Tecnologías encontradas",
                                            content = @Content(schema = @Schema(implementation = ApiResponseBaseMap.class))
                                    ),
                                    @ApiResponse(responseCode = "401", description = "Unauthorized")
                            }
                    )
            ),
            @RouterOperation(
                    path = PATH_DELETE_RELATE_CAPABILITIES_TECHNOLOGIES,
                    produces = {"application/json"},
                    method = org.springframework.web.bind.annotation.RequestMethod.DELETE,
                    beanClass = TechnologyCapabilityHandlerImpl.class,
                    beanMethod = "deleteCapabilitiesTechnologies",
                    operation = @Operation(
                            operationId = "deleteCapabilitiesTechnologies",
                            summary = "Delete technologies and relate with capabilities by technology IDs",
                            tags = { "Endpoints for webclients" },
                            security = @SecurityRequirement(name = "BearerAuth"),
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            in = ParameterIn.QUERY,
                                            name = "ids",
                                            description = "List of ids technologies separated by commas",
                                            example = "1,2,3",
                                            required = true
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Technologies and relate with capabilities deleted successfully",
                                            content = @Content(schema = @Schema(implementation = ApiResponseBase.class))
                                    ),
                                    @ApiResponse(responseCode = "401", description = "Unauthorized")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunctionTechnologyCapability(TechnologyCapabilityHandlerImpl technologyCapabilityHandler) {
        return RouterFunctions
                .route(POST(PATH_POST_TECHNOLOGY_CAPABILITY),
                        technologyCapabilityHandler::createCapabilityTechnologies)
                .andRoute(GET(PATH_GET_TECHNOLOGIES_BY_IDS_CAPABILITIES),
                        technologyCapabilityHandler::getTechnologiesListByCapabilityIds)
                .andRoute(DELETE(PATH_DELETE_RELATE_CAPABILITIES_TECHNOLOGIES),
                        technologyCapabilityHandler::deleteCapabilitiesTechnologies);
    }
}
