package org.citasalud.domain.port.out;

import org.citasalud.domain.model.FranjaHoraria;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FranjaHorariaRepositoryPort {
    List<FranjaHoraria> findByMedicoIdAndFecha(UUID medicoId, LocalDate fecha);
    Optional<FranjaHoraria> findByIdForUpdate(UUID id);
    FranjaHoraria save(FranjaHoraria franja);
}
