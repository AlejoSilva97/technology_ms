package com.example.technology.domain.usecase;

import com.example.technology.domain.enums.TechnicalMessage;
import com.example.technology.domain.exceptions.BusinessException;
import com.example.technology.domain.model.Technology;
import com.example.technology.domain.spi.TechnologyPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TechnologyUseCaseTest {

    private TechnologyPersistencePort techPersistencePort;
    private TechnologyUseCase technologyUseCase;

    @BeforeEach
    void setUp() {
        techPersistencePort = mock(TechnologyPersistencePort.class);
        technologyUseCase = new TechnologyUseCase(techPersistencePort);
    }

    @Nested
    class RegisterTechnologyTests {

        @Test
        void shouldRegisterTechnologySuccessfully() {
            Technology inputTech = new Technology(null, "Java", "Lenguaje de programación");
            Technology savedTech = new Technology(1L, "Java", "Lenguaje de programación");

            when(techPersistencePort.existByName("Java")).thenReturn(Mono.just(false));
            when(techPersistencePort.save(inputTech)).thenReturn(Mono.just(savedTech));

            StepVerifier.create(technologyUseCase.registerTechnology(inputTech))
                    .expectNext(savedTech)
                    .verifyComplete();

            verify(techPersistencePort, times(1)).existByName("Java");
            verify(techPersistencePort, times(1)).save(inputTech);
        }

        @Test
        void shouldFailWhenNameIsTooLong() {
            String longName = "A".repeat(51);
            Technology techWithLongName = new Technology(null, longName, "Descripción válida");

            StepVerifier.create(technologyUseCase.registerTechnology(techWithLongName))
                    .consumeErrorWith(throwable -> {
                        BusinessException exception = (BusinessException) throwable;
                        assertEquals(TechnicalMessage.TECHNOLOGY_NAME_TOOLONG, exception.getTechnicalMessage());
                    })
                    .verify();

            verifyNoInteractions(techPersistencePort);
        }

        @Test
        void shouldFailWhenDescriptionIsTooLong() {
            String longDescription = "B".repeat(91);
            Technology techWithLongDesc = new Technology(null, "Java", longDescription);

            StepVerifier.create(technologyUseCase.registerTechnology(techWithLongDesc))
                    .consumeErrorWith(throwable -> {
                        BusinessException exception = (BusinessException) throwable;
                        assertEquals(TechnicalMessage.TECHNOLOGY_DESCRIPTION_TOOLONG, exception.getTechnicalMessage());
                    })
                    .verify();

            verifyNoInteractions(techPersistencePort);
        }

        @Test
        void shouldFailWhenTechnologyAlreadyExists() {
            Technology duplicateTech = new Technology(null, "Java", "Descripción válida");

            when(techPersistencePort.existByName("Java")).thenReturn(Mono.just(true));

            StepVerifier.create(technologyUseCase.registerTechnology(duplicateTech))
                    .consumeErrorWith(throwable -> {
                        BusinessException exception = (BusinessException) throwable;
                        assertEquals(TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS, exception.getTechnicalMessage());
                    })
                    .verify();

            verify(techPersistencePort, times(1)).existByName("Java");
            verify(techPersistencePort, never()).save(any());
        }
    }

    @Nested
    class GetTechnologyByIdTests {

        @Test
        void shouldReturnTechnologyWhenExists() {
            Long techId = 1L;
            Technology expectedTech = new Technology(techId, "Java", "Descripción");

            when(techPersistencePort.findById(techId)).thenReturn(Mono.just(expectedTech));

            StepVerifier.create(technologyUseCase.getTechnologyById(techId))
                    .expectNext(expectedTech)
                    .verifyComplete();

            verify(techPersistencePort, times(1)).findById(techId);
        }

        @Test
        void shouldFailWhenTechnologyDoesNotExist() {
            Long techId = 99L;

            when(techPersistencePort.findById(techId)).thenReturn(Mono.empty());

            StepVerifier.create(technologyUseCase.getTechnologyById(techId))
                    .consumeErrorWith(throwable -> {
                        BusinessException exception = (BusinessException) throwable;
                        assertEquals(TechnicalMessage.TECHNOLOGY_NOT_EXISTS, exception.getTechnicalMessage());
                    })
                    .verify();

            verify(techPersistencePort, times(1)).findById(techId);
        }
    }
}