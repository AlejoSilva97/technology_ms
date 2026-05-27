package com.example.technology.infrastructure.entrypoints;

import com.example.technology.infrastructure.entrypoints.dto.TechnologyDTO;
import com.example.technology.infrastructure.entrypoints.handler.TechnologyHandlerImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/technologies",
                    method = RequestMethod.POST,
                    beanClass = TechnologyHandlerImpl.class,
                    beanMethod = "createTechnology",
                    operation = @Operation(
                            summary = "Registrar una nueva tecnología",
                            description = "Valida y almacena una tecnología si no existe previamente.",
                            operationId = "createTechnology",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = TechnologyDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Tecnología creada con éxito",
                                            content = @Content(schema = @Schema(implementation = String.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Error de validación o negocio")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/technologies/{id}",
                    method = RequestMethod.GET,
                    beanClass = TechnologyHandlerImpl.class,
                    beanMethod = "getById",
                    operation = @Operation(
                            summary = "Obtener una tecnología por ID",
                            description = "Busca en el sistema una tecnología basándose en su identificador único.",
                            operationId = "getById",
                            parameters = {
                                    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID de la tecnología", required = true)
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Tecnología encontrada",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = TechnologyDTO.class)
                                            )
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Tecnología no encontrada")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(TechnologyHandlerImpl technologyHandler) {
        return route(POST("/technologies"), technologyHandler::createTechnology)
                .andRoute(GET("/technologies/{id}"), technologyHandler::getById);
    }
}