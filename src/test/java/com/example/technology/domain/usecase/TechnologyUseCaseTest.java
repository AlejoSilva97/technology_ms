package com.example.technology.domain.usecase;

import com.example.technology.domain.constants.Constants;
import com.example.technology.domain.exceptions.TechnologyAlreadyExistsException;
import com.example.technology.domain.exceptions.TechnologyNotFoundException;
import com.example.technology.domain.model.Technology;
import com.example.technology.domain.spi.TechnologyPersistencePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnologyUseCaseTest {

    @Mock
    private TechnologyPersistencePort techPersistencePort;

    @InjectMocks
    private TechnologyUseCase technologyUseCase;

    @Test
    @DisplayName("Should register technology successfully when it does not exist")
    void should_RegisterTechnology_When_TechnologyDoesNotExist() {
        Technology inputTech = new Technology(null, "Spring Boot", "Reactive framework");
        Technology savedTech = new Technology(1L, "Spring Boot", "Reactive framework");

        when(techPersistencePort.existByName(inputTech.name())).thenReturn(Mono.just(false));
        when(techPersistencePort.save(inputTech)).thenReturn(Mono.just(savedTech));

        StepVerifier.create(technologyUseCase.registerTechnology(inputTech))
                .expectNext(savedTech)
                .verifyComplete();

        verify(techPersistencePort, times(1)).existByName(inputTech.name());
        verify(techPersistencePort, times(1)).save(inputTech);
    }

    @Test
    @DisplayName("Should throw TechnologyAlreadyExistsException when registering an existing technology name")
    void should_ThrowException_When_RegisteringExistingTechnologyName() {
        Technology inputTech = new Technology(null, "Spring Boot", "Reactive framework");
        String expectedMessage = String.format(Constants.TECHNOLOGY_ALREADY_EXISTS, inputTech.name());

        when(techPersistencePort.existByName(inputTech.name())).thenReturn(Mono.just(true));

        StepVerifier.create(technologyUseCase.registerTechnology(inputTech))
                .expectErrorMatches(throwable -> throwable instanceof TechnologyAlreadyExistsException
                        && throwable.getMessage().equals(expectedMessage))
                .verify();

        verify(techPersistencePort, times(1)).existByName(inputTech.name());
        verify(techPersistencePort, never()).save(any(Technology.class));
    }

    @Test
    @DisplayName("Should return Technology when found by ID")
    void should_ReturnTechnology_When_FoundById() {
        Long targetId = 1L;
        Technology expectedTech = new Technology(targetId, "Java", "Backend language");

        when(techPersistencePort.findById(targetId)).thenReturn(Mono.just(expectedTech));

        StepVerifier.create(technologyUseCase.getTechnologyById(targetId))
                .expectNext(expectedTech)
                .verifyComplete();

        verify(techPersistencePort, times(1)).findById(targetId);
    }

    @Test
    @DisplayName("Should throw TechnologyNotFoundException when ID does not exist")
    void should_ThrowException_When_IdDoesNotExist() {
        Long targetId = 99L;
        String expectedMessage = String.format(Constants.TECHNOLOGY_NOT_FOUND, targetId);

        when(techPersistencePort.findById(targetId)).thenReturn(Mono.empty());

        StepVerifier.create(technologyUseCase.getTechnologyById(targetId))
                .expectErrorMatches(throwable -> throwable instanceof TechnologyNotFoundException
                        && throwable.getMessage().equals(expectedMessage))
                .verify();

        verify(techPersistencePort, times(1)).findById(targetId);
    }
}