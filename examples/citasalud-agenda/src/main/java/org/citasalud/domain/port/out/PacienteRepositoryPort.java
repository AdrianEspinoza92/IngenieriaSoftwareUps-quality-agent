package org.citasalud.domain.port.out;

import org.citasalud.domain.model.Paciente;

import java.util.Optional;
import java.util.UUID;

public interface PacienteRepositoryPort {
    Optional<Paciente> findById(UUID id);
}
