package org.citasalud.domain.port.out;

import org.citasalud.domain.model.Medico;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicoRepositoryPort {
    List<Medico> findAll();
    Optional<Medico> findById(UUID id);
}
