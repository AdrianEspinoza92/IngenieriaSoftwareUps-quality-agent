package org.citasalud.infrastructure.persistence.adapter;

import org.citasalud.domain.model.Paciente;
import org.citasalud.domain.port.out.PacienteRepositoryPort;
import org.citasalud.infrastructure.persistence.mapper.PacienteMapper;
import org.citasalud.infrastructure.persistence.repository.PacienteJpaRepository;

import java.util.Optional;
import java.util.UUID;

public class PacienteRepositoryAdapter implements PacienteRepositoryPort {

    private final PacienteJpaRepository jpaRepository;

    public PacienteRepositoryAdapter(PacienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Paciente> findById(UUID id) {
        return jpaRepository.findById(id).map(PacienteMapper::toDomain);
    }
}
