package org.citasalud.infrastructure.persistence.adapter;

import org.citasalud.domain.model.Medico;
import org.citasalud.domain.port.out.MedicoRepositoryPort;
import org.citasalud.infrastructure.persistence.mapper.MedicoMapper;
import org.citasalud.infrastructure.persistence.repository.MedicoJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MedicoRepositoryAdapter implements MedicoRepositoryPort {

    private final MedicoJpaRepository jpaRepository;

    public MedicoRepositoryAdapter(MedicoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Medico> findAll() {
        return jpaRepository.findAll().stream()
                .map(MedicoMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Medico> findById(UUID id) {
        return jpaRepository.findById(id).map(MedicoMapper::toDomain);
    }
}
