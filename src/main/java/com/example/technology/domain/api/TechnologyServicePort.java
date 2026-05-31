package com.example.technology.domain.api;

import com.example.technology.domain.model.Technology;
import reactor.core.publisher.Mono;

public interface TechnologyServicePort {
    Mono<Technology> registerTechnology(Technology technology);
    Mono<Technology> getTechnologyById(Long id);
    Mono<Void> deleteById(Long id);
}
