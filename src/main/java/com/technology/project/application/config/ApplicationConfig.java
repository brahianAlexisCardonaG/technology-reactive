package com.technology.project.application.config;

import com.technology.project.domain.api.TechnologyCapabilityServicePort;
import com.technology.project.domain.api.TechnologyServicePort;
import com.technology.project.domain.spi.TechnologyPersistencePort;
import com.technology.project.domain.usecase.TechnologyUseCase;
import com.technology.project.domain.usecase.util.ValidationTechnologyCapacity;
import com.technology.project.infraestructure.adapters.persistenceadapter.TechnologyPersistenceAdapter;
import com.technology.project.infraestructure.adapters.persistenceadapter.mapper.TechnologyEntityMapper;
import com.technology.project.infraestructure.adapters.persistenceadapter.repository.CapabilityTechnologyRepository;
import com.technology.project.infraestructure.adapters.persistenceadapter.repository.TechnologyRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final TechnologyRespository technologyRespository;
    private final TechnologyEntityMapper technologyEntityMapper;
    private final CapabilityTechnologyRepository capabilityTechnologyRepository;

    @Bean
    public TechnologyPersistencePort technologyPersistencePort() {
        return new TechnologyPersistenceAdapter(technologyRespository
                                                ,technologyEntityMapper
                                                , capabilityTechnologyRepository);
    }

    @Bean
    public TechnologyServicePort technologyServicePort(TechnologyPersistencePort technologyPersistencePort,
                                                       ValidationTechnologyCapacity validationTechnologyCapacity) {
        return new TechnologyUseCase(technologyPersistencePort, validationTechnologyCapacity);
    }

    @Bean
    public TechnologyCapabilityServicePort technologyCapabilityServicePort(TechnologyPersistencePort technologyPersistencePort,
                                                                           ValidationTechnologyCapacity validationTechnologyCapacity) {
        return new TechnologyUseCase(technologyPersistencePort, validationTechnologyCapacity);
    }
}
