package com.technology.project.infraestructure.adapters.persistenceadapter;

import com.technology.project.domain.model.Technology;
import com.technology.project.infraestructure.adapters.persistenceadapter.entity.CapabilityTechnologyEntity;
import com.technology.project.infraestructure.adapters.persistenceadapter.entity.TechnologyEntity;
import com.technology.project.infraestructure.adapters.persistenceadapter.mapper.TechnologyEntityMapper;
import com.technology.project.infraestructure.adapters.persistenceadapter.repository.CapabilityTechnologyRepository;
import com.technology.project.infraestructure.adapters.persistenceadapter.repository.TechnologyRespository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TechnologyPersistenceAdapterTest {
    @Mock
    private TechnologyRespository technologyRespository;

    @Mock
    private CapabilityTechnologyRepository capabilityTechnologyRepository;

    @Mock
    private TechnologyEntityMapper technologyEntityMapper;

    private TechnologyPersistenceAdapter adapter;

    @BeforeEach
    public void setUp() {
        adapter = new TechnologyPersistenceAdapter(technologyRespository, technologyEntityMapper, capabilityTechnologyRepository);
    }

    // Caso: findByName. Se devuelve true si se encuentra la tecnología.
    @Test
    public void testFindByNameExists() {
        String nombre = "TechA";
        TechnologyEntity techEntity = new TechnologyEntity();
        Technology techModel = new Technology();

        when(technologyRespository.findByName(nombre)).thenReturn(Mono.just(techEntity));
        when(technologyEntityMapper.toModel(techEntity)).thenReturn(techModel);

        Mono<Boolean> result = adapter.findByName(nombre);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void testFindByNameNotExists() {
        String nombre = "TechNoExiste";

        when(technologyRespository.findByName(nombre)).thenReturn(Mono.empty());

        Mono<Boolean> result = adapter.findByName(nombre);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void testExistsByIdExists() {
        Long idTech = 1L;
        TechnologyEntity techEntity = new TechnologyEntity();
        Technology techModel = new Technology();

        when(technologyRespository.findById(idTech)).thenReturn(Mono.just(techEntity));
        when(technologyEntityMapper.toModel(techEntity)).thenReturn(techModel);

        Mono<Boolean> result = adapter.existsById(idTech);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void testExistsByIdNotExists() {
        Long idTech = 1L;

        when(technologyRespository.findById(idTech)).thenReturn(Mono.empty());

        Mono<Boolean> result = adapter.existsById(idTech);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void testFindTechnologiesByCapability() {
        Long capabilityId = 100L;
        CapabilityTechnologyEntity ctEntity1 = new CapabilityTechnologyEntity(null, capabilityId, 10L);
        CapabilityTechnologyEntity ctEntity2 = new CapabilityTechnologyEntity(null, capabilityId, 20L);

        when(capabilityTechnologyRepository.findByIdCapability(capabilityId))
                .thenReturn(Flux.just(ctEntity1, ctEntity2));

        Mono<List<Long>> result = adapter.findTechnologiesByCapability(capabilityId);

        StepVerifier.create(result)
                .expectNextMatches(list -> list.size() == 2 &&
                        list.containsAll(Arrays.asList(10L, 20L)))
                .verifyComplete();
    }

    @Test
    public void testSaveRelations() {
        Long capabilityId = 200L;
        List<Long> techIds = Arrays.asList(30L, 40L, 50L);
        // Simulamos que saveAll retorna las entidades guardadas.
        List<CapabilityTechnologyEntity> savedEntities = Arrays.asList(
                new CapabilityTechnologyEntity(1L, capabilityId, 30L),
                new CapabilityTechnologyEntity(2L, capabilityId, 40L),
                new CapabilityTechnologyEntity(3L, capabilityId, 50L)
        );

        when(capabilityTechnologyRepository.saveAll(anyList()))
                .thenReturn(Flux.fromIterable(savedEntities));

        Mono<Void> result = adapter.saveRelations(capabilityId, techIds);

        StepVerifier.create(result)
                .verifyComplete();

        ArgumentCaptor<List<CapabilityTechnologyEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(capabilityTechnologyRepository).saveAll(captor.capture());
        List<CapabilityTechnologyEntity> capturedList = captor.getValue();
        assertEquals(techIds.size(), capturedList.size());
        for (int i = 0; i < techIds.size(); i++) {
            assertEquals(capabilityId, capturedList.get(i).getIdCapability());
            assertEquals(techIds.get(i), capturedList.get(i).getIdTechnology());
        }
    }

    @Test
    public void testFindTechnologiesListByCapability() {
        Long capabilityId = 300L;
        CapabilityTechnologyEntity ctEntity1 = new CapabilityTechnologyEntity(null, capabilityId, 60L);
        CapabilityTechnologyEntity ctEntity2 = new CapabilityTechnologyEntity(null, capabilityId, 70L);

        TechnologyEntity techEntity1 = new TechnologyEntity();
        TechnologyEntity techEntity2 = new TechnologyEntity();
        Technology techModel1 = new Technology();
        Technology techModel2 = new Technology();

        when(capabilityTechnologyRepository.findByIdCapability(capabilityId))
                .thenReturn(Flux.just(ctEntity1, ctEntity2));

        when(technologyRespository.findAllById(Arrays.asList(60L, 70L)))
                .thenReturn(Flux.just(techEntity1, techEntity2));

        when(technologyEntityMapper.toModel(techEntity1)).thenReturn(techModel1);
        when(technologyEntityMapper.toModel(techEntity2)).thenReturn(techModel2);

        Mono<List<Technology>> result = adapter.findTechnologiesListByCapability(capabilityId);

        StepVerifier.create(result)
                .expectNextMatches(list -> list.size() == 2)
                .verifyComplete();
    }

    @Test
    public void testExistsCapabilityByIdExists() {
        Long capabilityId = 400L;
        CapabilityTechnologyEntity ctEntity = new CapabilityTechnologyEntity(null, capabilityId, 80L);

        when(capabilityTechnologyRepository.findByIdCapability(capabilityId))
                .thenReturn(Flux.just(ctEntity));

        Mono<Boolean> result = adapter.existsCapabilityById(capabilityId);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void testExistsCapabilityByIdNotExists() {
        Long capabilityId = 400L;

        when(capabilityTechnologyRepository.findByIdCapability(capabilityId))
                .thenReturn(Flux.empty());

        Mono<Boolean> result = adapter.existsCapabilityById(capabilityId);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void testFindByIds() {
        List<Long> techIds = Arrays.asList(90L, 100L);
        TechnologyEntity techEntity1 = new TechnologyEntity();
        TechnologyEntity techEntity2 = new TechnologyEntity();
        Technology techModel1 = new Technology();
        Technology techModel2 = new Technology();

        when(technologyRespository.findAllById(techIds))
                .thenReturn(Flux.just(techEntity1, techEntity2));
        when(technologyEntityMapper.toModel(techEntity1)).thenReturn(techModel1);
        when(technologyEntityMapper.toModel(techEntity2)).thenReturn(techModel2);

        Flux<Technology> result = adapter.findByIds(techIds);

        StepVerifier.create(result)
                .expectNext(techModel1, techModel2)
                .verifyComplete();
    }

    @Test
    public void testSave() {
        Technology tech1 = new Technology();
        Technology tech2 = new Technology();
        TechnologyEntity entity1 = new TechnologyEntity();
        TechnologyEntity entity2 = new TechnologyEntity();

        when(technologyEntityMapper.toEntity(tech1)).thenReturn(entity1);
        when(technologyEntityMapper.toEntity(tech2)).thenReturn(entity2);

        when(technologyRespository.saveAll(Arrays.asList(entity1, entity2)))
                .thenReturn(Flux.just(entity1, entity2));

        when(technologyEntityMapper.toModel(entity1)).thenReturn(tech1);
        when(technologyEntityMapper.toModel(entity2)).thenReturn(tech2);

        Flux<Technology> result = adapter.save(Flux.just(tech1, tech2));

        StepVerifier.create(result)
                .expectNext(tech1, tech2)
                .verifyComplete();
    }

    @Test
    public void testDeleteCapabilitiesTechnologies() {
        List<Long> techIdsToDelete = Arrays.asList(10L, 20L);

        CapabilityTechnologyEntity ct1 = new CapabilityTechnologyEntity(1L, 100L, 10L);
        CapabilityTechnologyEntity ct2 = new CapabilityTechnologyEntity(2L, 100L, 20L);
        CapabilityTechnologyEntity ct3 = new CapabilityTechnologyEntity(3L, 100L, 30L); // Esta no se debe eliminar

        when(capabilityTechnologyRepository.findAll()).thenReturn(Flux.just(ct1, ct2, ct3));

        // Usamos ArgumentCaptor para capturar la lista que se pasa a deleteAll.
        ArgumentCaptor<List<CapabilityTechnologyEntity>> captor = ArgumentCaptor.forClass(List.class);
        when(capabilityTechnologyRepository.deleteAll(anyList()))
                .thenReturn(Mono.empty());

        Mono<Void> result = adapter.deleteCapabilitiesTechnologies(techIdsToDelete);

        StepVerifier.create(result)
                .verifyComplete();

        // Validamos que deleteAll se haya llamado con
        // una lista que solo incluya las entidades cuyo idTechnology está en techIdsToDelete.
        verify(capabilityTechnologyRepository).deleteAll(captor.capture());
        List<CapabilityTechnologyEntity> capturedList = captor.getValue();
        assertEquals(2, capturedList.size());
        capturedList.forEach(entity -> assertTrue(techIdsToDelete.contains(entity.getIdTechnology())));
    }

    @Test
    public void testDeleteTechnologies() {
        List<Long> techIdsToDelete = Arrays.asList(50L, 60L);

        TechnologyEntity techEntity1 = new TechnologyEntity();
        TechnologyEntity techEntity2 = new TechnologyEntity();

        when(technologyRespository.findAllById(techIdsToDelete))
                .thenReturn(Flux.just(techEntity1, techEntity2));

        // Capturamos el argumento que se envía a deleteAll.
        ArgumentCaptor<List<TechnologyEntity>> captor = ArgumentCaptor.forClass(List.class);
        when(technologyRespository.deleteAll(anyList())).thenReturn(Mono.empty());

        Mono<Void> result = adapter.deleteTechnologies(techIdsToDelete);

        StepVerifier.create(result)
                .verifyComplete();

        verify(technologyRespository).deleteAll(captor.capture());
        List<TechnologyEntity> capturedList = captor.getValue();
        assertEquals(2, capturedList.size());
    }
}
