package com.technology.project.domain.usecase;

import com.technology.project.domain.enums.TechnicalMessage;
import com.technology.project.domain.exception.BusinessException;
import com.technology.project.domain.model.Technology;
import com.technology.project.domain.spi.TechnologyPersistencePort;
import com.technology.project.domain.usecase.util.ValidationTechnologyCapacity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TechnologyUseCaseTest {

    @Mock
    private TechnologyPersistencePort persistencePort;

    @Mock
    private ValidationTechnologyCapacity validator;

    @InjectMocks
    private TechnologyUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new TechnologyUseCase(persistencePort, validator);
    }

    @Test
    void save_shouldSaveUniqueTechnologies() {
        Technology tech = new Technology(1L, "Java", "Backend");

        when(validator.validateLengthWords(any(Technology.class)))
                .thenReturn(Mono.empty());
        when(persistencePort.existByName("Java")).thenReturn(Mono.just(false));
        when(persistencePort.save(any())).thenReturn(Mono.just(tech));

        StepVerifier.create(useCase.save(tech))
                .expectNext(tech)
                .verifyComplete();

        verify(persistencePort).save(any(Technology.class));
    }

    @Test
    void save_shouldThrowIfTechnologyExists() {
        Technology tech = new Technology(1L, "Java", "Backend");

        when(persistencePort.existByName("Java")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.save(tech))
                .expectErrorMatches(error -> error instanceof BusinessException &&
                        ((BusinessException) error).getTechnicalMessage() == TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS)
                .verify();

        verify(persistencePort, never()).save(any());
    }

    @Test
    void saveCapabilityTechnologies_shouldValidateAndSave() {
        Long capabilityId = 1L;
        List<Long> techIds = List.of(1L, 2L);

        when(persistencePort.existsById(1L)).thenReturn(Mono.just(true));
        when(persistencePort.existsById(2L)).thenReturn(Mono.just(true));
        when(persistencePort.findTechnologiesByCapability(capabilityId)).thenReturn(Mono.just(List.of(1L)));
        when(validator.validateHasDuplicatesTechnologies(any(), any())).thenReturn(Mono.empty());
        when(validator.validateNumberTechnologies(any(), any())).thenReturn(Mono.empty());
        when(persistencePort.saveRelations(capabilityId, techIds)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.saveCapabilityTechnologies(capabilityId, techIds))
                .verifyComplete();

        verify(persistencePort).saveRelations(capabilityId, techIds);
    }

    @Test
    void saveCapabilityTechnologies_shouldThrowIfTechNotExists() {
        Long capabilityId = 1L;
        List<Long> techIds = List.of(1L, 2L);

        when(persistencePort.existsById(1L)).thenReturn(Mono.just(true));
        when(persistencePort.existsById(2L)).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.saveCapabilityTechnologies(capabilityId, techIds))
                .expectErrorMatches(error -> error instanceof BusinessException &&
                        ((BusinessException) error).getTechnicalMessage() == TechnicalMessage.TECHNOLOGY_NOT_EXISTS)
                .verify();
    }

    @Test
    void findTechnologiesByCapability_shouldReturnList() {
        Long capabilityId = 1L;
        List<Technology> techList = List.of(new Technology(1L, "Java", "Backend"));

        when(persistencePort.existsCapabilityById(capabilityId)).thenReturn(Mono.just(true));
        when(persistencePort.findTechnologiesListByCapability(capabilityId)).thenReturn(Mono.just(techList));

        StepVerifier.create(useCase.findTechnologiesByCapability(capabilityId))
                .expectNext(techList)
                .verifyComplete();
    }

    @Test
    void findTechnologiesByCapability_shouldThrowIfCapabilityNotExists() {
        when(persistencePort.existsCapabilityById(1L)).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.findTechnologiesByCapability(1L))
                .expectErrorMatches(error -> error instanceof BusinessException &&
                        ((BusinessException) error).getTechnicalMessage() == TechnicalMessage.CAPABILITIES_NOT_EXISTS)
                .verify();
    }

    @Test
    void getTechnologiesByIds_shouldReturnTechnologies() {
        List<Long> ids = List.of(1L, 2L);
        Technology tech = new Technology(1L, "Java", "Backend");

        when(persistencePort.findByIds(ids)).thenReturn(Mono.just(List.of(tech)));

        StepVerifier.create(useCase.getTechnologiesByIds(ids))
                .expectNextMatches(list -> list.size() == 1 && list.get(0).equals(tech))
                .verifyComplete();
    }

    @Test
    void getTechnologiesByIds_shouldReturnDefaultIfEmpty() {
        List<Long> ids = List.of(1L);

        when(persistencePort.findByIds(ids)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getTechnologiesByIds(ids))
                .expectErrorMatches(error -> error instanceof BusinessException &&
                        ((BusinessException) error).getTechnicalMessage() == TechnicalMessage.TECHNOLOGY_NOT_EXISTS)
                .verify();
    }

    @Test
    void deleteCapabilityTechnologies_shouldDeleteSuccessfully() {
        List<Long> technologyIds = List.of(1L, 2L);

        // Se simula la búsqueda de capacidades relacionadas: se retorna una única capacidad
        when(persistencePort.findCapabilitiesByTechnologiesIds(technologyIds))
                .thenReturn(Mono.just(List.of(100L)));

        // Se configuran las operaciones de borrado para completar sin error
        when(persistencePort.deleteCapabilitiesTechnologies(technologyIds)).thenReturn(Mono.empty());
        when(persistencePort.deleteTechnologies(technologyIds)).thenReturn(Mono.empty());

        Mono<Void> result = useCase.deleteCapabilityTechnologies(technologyIds);

        StepVerifier.create(result)
                .verifyComplete();

        verify(persistencePort).deleteCapabilitiesTechnologies(technologyIds);
        verify(persistencePort).deleteTechnologies(technologyIds);
    }

    @Test
    void deleteCapabilityTechnologies_shouldThrowErrorIfMultipleCapabilitiesFound() {
        List<Long> technologyIds = List.of(1L, 2L);

        // Se retorna más de una capacidad para provocar la excepción
        when(persistencePort.findCapabilitiesByTechnologiesIds(technologyIds))
                .thenReturn(Mono.just(List.of(100L, 200L)));

        Mono<Void> result = useCase.deleteCapabilityTechnologies(technologyIds);

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof BusinessException &&
                        ((BusinessException) error).getTechnicalMessage() ==
                                TechnicalMessage.CAPABILITIES_TECHNOLOGIES_MORE_ONE_RELATE)
                .verify();

        verify(persistencePort, never()).deleteCapabilitiesTechnologies(any());
        verify(persistencePort, never()).deleteTechnologies(any());
    }
}