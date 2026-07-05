package org.citasalud.infrastructure.persistence.adapter;

import org.citasalud.domain.model.Cita;
import org.citasalud.domain.port.out.CitaRepositoryPort;
import org.citasalud.infrastructure.persistence.mapper.CitaMapper;
import org.citasalud.infrastructure.persistence.repository.CitaJpaRepository;

import java.util.Optional;
import java.util.UUID;

public class CitaRepositoryAdapter implements CitaRepositoryPort {

    private final CitaJpaRepository jpaRepository;

    public CitaRepositoryAdapter(CitaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cita save(Cita cita) {
        return CitaMapper.toDomain(jpaRepository.save(CitaMapper.toEntity(cita)));
    }

    @Override
    public Optional<Cita> findById(UUID id) {
        return jpaRepository.findById(id).map(CitaMapper::toDomain);
    }

    @Override
    public long countAll() {
        return jpaRepository.count();
    }
}
