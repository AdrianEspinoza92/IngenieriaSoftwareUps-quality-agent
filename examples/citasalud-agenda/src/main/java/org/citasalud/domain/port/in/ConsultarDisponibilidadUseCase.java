package org.citasalud.domain.port.in;

import org.citasalud.domain.model.FranjaHoraria;
import org.citasalud.domain.model.Medico;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ConsultarDisponibilidadUseCase {
    List<Medico> listarMedicos();
    List<FranjaHoraria> consultarDisponibilidad(UUID medicoId, LocalDate fecha);
}
