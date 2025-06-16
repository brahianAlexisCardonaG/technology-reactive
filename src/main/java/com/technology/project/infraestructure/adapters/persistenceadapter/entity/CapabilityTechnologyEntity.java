package com.technology.project.infraestructure.adapters.persistenceadapter.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("capability_technology")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CapabilityTechnologyEntity {
    @Id
    private Long id;

    // Referencia a la capability
    @Column("id_capability")
    private Long idCapability;

    // Referencia (débil) a la tecnología en otro esquema o RDBMS
    @Column("id_technology")
    private Long idTechnology;
}