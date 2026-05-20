package com.example.resilient_api.domain.api;

import com.example.resilient_api.domain.model.Technology;
import reactor.core.publisher.Mono;

public interface TechnologyServicePort {
    Mono<Technology> registerTechnology(Technology technology);
    Mono<Technology> getTechnologyById(Long id);
}
