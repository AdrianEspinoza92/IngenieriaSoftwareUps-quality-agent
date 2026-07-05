package org.citasalud.domain.port.out;

import org.citasalud.domain.model.Cita;

import java.util.Optional;
import java.util.UUID;

public interface CitaRepositoryPort {
    Cita save(Cita cita);
    Optional<Cita> findById(UUID id);
    long countAll();
}
