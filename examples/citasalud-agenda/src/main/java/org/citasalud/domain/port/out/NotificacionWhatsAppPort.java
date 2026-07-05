package org.citasalud.domain.port.out;

import org.citasalud.domain.model.Cita;
import org.citasalud.domain.model.FranjaHoraria;
import org.citasalud.domain.model.Medico;
import org.citasalud.domain.model.Paciente;

public interface NotificacionWhatsAppPort {
    void enviarConfirmacion(Cita cita, Medico medico, FranjaHoraria franja, Paciente paciente);
}
