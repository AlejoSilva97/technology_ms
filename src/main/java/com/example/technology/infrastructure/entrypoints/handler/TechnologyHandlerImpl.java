package com.example.technology.infrastructure.entrypoints.handler;

import com.example.technology.domain.api.TechnologyServicePort;
import com.example.technology.domain.constants.Constants;
import com.example.technology.infrastructure.entrypoints.dto.TechnologyDTO;
import com.example.technology.infrastructure.entrypoints.mapper.TechnologyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class TechnologyHandlerImpl {

    private final TechnologyServicePort technologyServicePort;
    private final TechnologyMapper technologyMapper;

    public Mono<ServerResponse> createTechnology(ServerRequest request) {
        return request.bodyToMono(TechnologyDTO.class)
                .flatMap(technology -> technologyServicePort.registerTechnology(technologyMapper.toTechnology(technology))
                        .doOnSuccess(savedTechnology -> log.info("Technology created successfully"))
                )
                .flatMap(savedTechnology -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(Constants.TECHNOLOGY_CREATED));
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        String idString = request.pathVariable("id");
        Long id = Long.parseLong(idString);
        return technologyServicePort.getTechnologyById(id)
                .flatMap(technology -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(technologyMapper.toTechnologyDTO(technology)));
    }

    public Mono<ServerResponse> deleteById(ServerRequest request) {
        String idString = request.pathVariable("id");
        Long id = Long.parseLong(idString);

        return technologyServicePort.deleteById(id)
                .then(ServerResponse.noContent().build());
    }
}
