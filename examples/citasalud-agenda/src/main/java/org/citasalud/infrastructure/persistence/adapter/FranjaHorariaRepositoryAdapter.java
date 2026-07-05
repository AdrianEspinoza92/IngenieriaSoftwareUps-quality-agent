package org.citasalud.infrastructure.persistence.adapter;

import org.citasalud.domain.model.FranjaHoraria;
import org.citasalud.domain.port.out.FranjaHorariaRepositoryPort;
import org.citasalud.infrastructure.persistence.entity.FranjaHorariaEntity;
import org.citasalud.infrastructure.persistence.mapper.FranjaHorariaMapper;
import org.citasalud.infrastructure.persistence.repository.FranjaHorariaJpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FranjaHorariaRepositoryAdapter implements FranjaHorariaRepositoryPort {

    private final FranjaHorariaJpaRepository jpaRepository;

    public FranjaHorariaRepositoryAdapter(FranjaHorariaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<FranjaHoraria> findByMedicoIdAndFecha(UUID medicoId, LocalDate fecha) {
        return jpaRepository.findByMedicoIdAndFecha(medicoId, fecha).stream()
                .map(FranjaHorariaMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<FranjaHoraria> findByIdForUpdate(UUID id) {
        return jpaRepository.findByIdForUpdate(id).map(FranjaHorariaMapper::toDomain);
    }

    @Override
    public FranjaHoraria save(FranjaHoraria franja) {
        FranjaHorariaEntity entity = jpaRepository.findById(franja.getId())
                .orElseGet(() -> FranjaHorariaMapper.toEntity(franja));
        entity.setEstado(franja.getEstado());
        return FranjaHorariaMapper.toDomain(jpaRepository.save(entity));
    }
}
