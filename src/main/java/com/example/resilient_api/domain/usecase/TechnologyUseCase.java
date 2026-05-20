package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.model.Technology;
import com.example.resilient_api.domain.spi.TechnologyPersistencePort;
import com.example.resilient_api.domain.api.TechnologyServicePort;
import reactor.core.publisher.Mono;

public class TechnologyUseCase implements TechnologyServicePort {

    private final TechnologyPersistencePort techPersistencePort;

    public TechnologyUseCase(TechnologyPersistencePort techPersistencePort) {
        this.techPersistencePort = techPersistencePort;
    }

    @Override
    public Mono<Technology> registerTechnology(Technology technology) {
        return Mono.just(technology)
                .filter(tech -> tech.name().length() <= 50)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_NAME_TOOLONG)))
                .filter(tech -> tech.description().length() <= 90)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_DESCRIPTION_TOOLONG)))
                .flatMap(tech -> techPersistencePort.existByName(tech.name())
                        .flatMap(exists -> exists
                                ? Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS))
                                : Mono.just(tech)
                        )
                )
                .flatMap(techPersistencePort::save);
    }

    @Override
    public Mono<Technology> getTechnologyById(Long id) {
        return techPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_NOT_EXISTS)));
    }
}
