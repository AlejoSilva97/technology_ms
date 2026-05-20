package com.example.resilient_api.infrastructure.entrypoints.handler;

import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.exceptions.TechnicalException;
import com.example.resilient_api.infrastructure.entrypoints.util.APIResponse;
import com.example.resilient_api.infrastructure.entrypoints.util.ErrorDTO;
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

import java.time.Instant;
import java.util.List;

@Component
@Order(-2) // Prioridad alta para que intercepte antes que el manejador por defecto de Spring Boot
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
        // Captura absolutamente cualquier petición que haya fallado en el flujo funcional
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        // Recuperamos la excepción real que ocurrió en el flujo reactivo
        Throwable error = getError(request);

        if (error instanceof BusinessException businessEx) {
            log.warn("Business rule violation: {}", businessEx.getMessage());
            return buildResponse(HttpStatus.BAD_REQUEST, TechnicalMessage.INVALID_PARAMETERS,
                    List.of(mapToErrorDTO(businessEx.getTechnicalMessage())));
        }

        if (error instanceof TechnicalException technicalEx) {
            log.error("Technical exception occurred: ", technicalEx);
            return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, TechnicalMessage.INTERNAL_ERROR,
                    List.of(mapToErrorDTO(technicalEx.getTechnicalMessage())));
        }

        // Errores inesperados (NullPointerException, errores de base de datos no controlados, etc.)
        log.error("Unexpected system error: ", error);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, TechnicalMessage.INTERNAL_ERROR,
                List.of(ErrorDTO.builder()
                        .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                        .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                        .build()));
    }

    private ErrorDTO mapToErrorDTO(TechnicalMessage message) {
        return ErrorDTO.builder()
                .code(message.getCode())
                .message(message.getMessage())
                .param(message.getParam())
                .build();
    }

    private Mono<ServerResponse> buildResponse(HttpStatus httpStatus, TechnicalMessage error, List<ErrorDTO> errors) {
        APIResponse apiErrorResponse = APIResponse.builder()
                .code(error.getCode())
                .message(error.getMessage())
                .date(Instant.now().toString())
                .errors(errors)
                .build();

        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(apiErrorResponse);
    }
}