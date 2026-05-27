package com.example.technology.application.config;

import com.example.technology.domain.spi.TechnologyPersistencePort;
import com.example.technology.domain.usecase.TechnologyUseCase;
import com.example.technology.domain.api.TechnologyServicePort;
import com.example.technology.infrastructure.adapters.persistenceadapter.TechnologyPersistenceAdapter;
import com.example.technology.infrastructure.adapters.persistenceadapter.mapper.TechnologyEntityMapper;
import com.example.technology.infrastructure.adapters.persistenceadapter.repository.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {
        private final TechnologyRepository technologyRepository;
        private final TechnologyEntityMapper technologyEntityMapper;

        @Bean
        public TechnologyPersistencePort technologiesPersistencePort() {
                return new TechnologyPersistenceAdapter(technologyRepository, technologyEntityMapper);
        }

        @Bean
        public TechnologyServicePort technologiesServicePort(TechnologyPersistencePort technologyPersistencePort){
                return new TechnologyUseCase(technologyPersistencePort);
        }
}
