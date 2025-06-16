package com.technology.project.infraestructure.entrypoints.technology;

import com.technology.project.infraestructure.entrypoints.technology.dto.TechnologyDto;
import com.technology.project.infraestructure.entrypoints.technology.handler.TechnologyHandlerImpl;
import com.technology.project.infraestructure.entrypoints.util.response.ApiResponseBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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
public class RouterRestTechnology {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = PATH_POST_TECHNOLOGY,
                    produces = {"application/json"},
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = TechnologyHandlerImpl.class,
                    beanMethod = "create",
                    operation = @Operation(
                            operationId = "createTechnology",
                            summary = "Create list of technologies",
                            tags = { "Endpoints Technologies" },
                            security = @SecurityRequirement(name = "BearerAuth"),
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnologyDto.class)))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Technologies create successfully",
                                            content = @Content(schema = @Schema(implementation = ApiResponseBase.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validation"
                                    ),
                                    @ApiResponse(responseCode = "401", description = "Unauthorized")
                            }
                    )
            ),
            @RouterOperation(
                    path = PATH_GET_TECHNOLOGIES_BY_IDS,
                    produces = {"application/json"},
                    method = org.springframework.web.bind.annotation.RequestMethod.GET,
                    beanClass = TechnologyHandlerImpl.class,
                    beanMethod = "getTechnologiesByIds",
                    operation = @Operation(
                            operationId = "getTechnologiesByIds",
                            summary = "Obtain technologies by IDs",
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
                                            description = "Technologies found",
                                            content = @Content(schema = @Schema(implementation = ApiResponseBase.class))
                                    ),
                                    @ApiResponse(responseCode = "401", description = "Unauthorized")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunctionTechnology(TechnologyHandlerImpl technologyHandler) {
        return RouterFunctions
                .route(POST(PATH_POST_TECHNOLOGY), technologyHandler::create)
                .andRoute(GET(PATH_GET_TECHNOLOGIES_BY_IDS),
                        technologyHandler::getTechnologiesByIds);
    }
}
