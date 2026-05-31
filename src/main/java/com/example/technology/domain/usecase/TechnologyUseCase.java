package com.example.technology.domain.usecase;

import com.example.technology.domain.constants.Constants;
import com.example.technology.domain.exceptions.TechnologyAlreadyExistsException;
import com.example.technology.domain.exceptions.TechnologyNotFoundException;
import com.example.technology.domain.model.Technology;
import com.example.technology.domain.spi.TechnologyPersistencePort;
import com.example.technology.domain.api.TechnologyServicePort;
import reactor.core.publisher.Mono;

public class TechnologyUseCase implements TechnologyServicePort {

    private final TechnologyPersistencePort techPersistencePort;

    public TechnologyUseCase(TechnologyPersistencePort techPersistencePort) {
        this.techPersistencePort = techPersistencePort;
    }

    @Override
    public Mono<Technology> registerTechnology(Technology technology) {
        return techPersistencePort.existByName(technology.name())
                .flatMap(exists -> exists
                        ? Mono.error(new TechnologyAlreadyExistsException(String.format(Constants.TECHNOLOGY_ALREADY_EXISTS, technology.name())))
                        :techPersistencePort.save(technology));
    }

    @Override
    public Mono<Technology> getTechnologyById(Long id) {
        return techPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new TechnologyNotFoundException(String.format(Constants.TECHNOLOGY_NOT_FOUND, id))));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return techPersistencePort.existById(id)
                .flatMap(exists -> exists
                        ? techPersistencePort.deleteById(id)
                        : Mono.error(new TechnologyNotFoundException(String.format(Constants.TECHNOLOGY_NOT_FOUND, id))));
    }
}
