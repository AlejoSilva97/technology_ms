package com.example.technology.infrastructure.entrypoints.handler;

import com.example.technology.domain.constants.Constants;
import com.example.technology.domain.exceptions.InvalidFieldException;
import com.example.technology.domain.exceptions.TechnologyAlreadyExistsException;
import com.example.technology.domain.exceptions.TechnologyNotFoundException;
import com.example.technology.infrastructure.entrypoints.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
@Slf4j
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  WebProperties webProperties,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
        this.setMessageReaders(configurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);

        if (error instanceof TechnologyAlreadyExistsException technologyAlreadyExistsException) {
            log.warn("Business rule violation: {}", technologyAlreadyExistsException.getMessage());
            return buildErrorDTO(HttpStatus.CONFLICT, Constants.TECHNOLOGY_ALREADY_EXISTS_CODE, technologyAlreadyExistsException.getMessage());
        }

        if (error instanceof TechnologyNotFoundException technologyNotFoundException) {
            log.warn("Business rule violation: {}", technologyNotFoundException.getMessage());
            return buildErrorDTO(HttpStatus.NOT_FOUND, Constants.TECHNOLOGY_NOT_FOUND_CODE, technologyNotFoundException.getMessage());
        }

        if (error instanceof InvalidFieldException invalidFieldException) {
            log.warn("Business rule violation: {}", invalidFieldException.getMessage());
            return buildErrorDTO(HttpStatus.BAD_REQUEST, Constants.INVALID_FIELD_CODE, invalidFieldException.getMessage());
        }

        log.error("Unexpected system error: ", error);
        return buildErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR, Constants.INTERNAL_ERROR_CODE, Constants.INTERNAL_ERROR);
    }

    private Mono<ServerResponse> buildErrorDTO(HttpStatus status, String code, String message) {
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code(code)
                .message(message)
                .build();

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }
}