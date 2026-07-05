package org.citasalud.domain.port.in;

import org.citasalud.domain.model.Cita;

import java.time.LocalDate;
import java.util.UUID;

public interface ReservarCitaUseCase {
    Cita reservar(UUID pacienteId, UUID medicoId, UUID franjaHorariaId, LocalDate fecha);
    Cita buscarPorId(UUID citaId);
}
