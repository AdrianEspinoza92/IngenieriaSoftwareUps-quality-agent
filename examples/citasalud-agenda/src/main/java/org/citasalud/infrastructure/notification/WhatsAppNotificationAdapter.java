package org.citasalud.infrastructure.notification;

import org.citasalud.domain.model.*;
import org.citasalud.domain.port.out.NotificacionWhatsAppPort;
import org.citasalud.infrastructure.persistence.entity.NotificacionWhatsAppEntity;
import org.citasalud.infrastructure.persistence.repository.NotificacionWhatsAppJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

public class WhatsAppNotificationAdapter implements NotificacionWhatsAppPort {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppNotificationAdapter.class);

    private final NotificacionWhatsAppJpaRepository notificacionRepo;

    public WhatsAppNotificationAdapter(NotificacionWhatsAppJpaRepository notificacionRepo) {
        this.notificacionRepo = notificacionRepo;
    }

    @Override
    public void enviarConfirmacion(Cita cita, Medico medico, FranjaHoraria franja, Paciente paciente) {
        String mensaje = String.format(
                "Cita confirmada. Médico: %s (%s). Fecha: %s, Hora: %s-%s. Referencia: %s.",
                medico.getNombre(),
                medico.getEspecialidad(),
                franja.getFecha(),
                franja.getHoraInicio(),
                franja.getHoraFin(),
                cita.getNumeroReferencia());

        log.info("WhatsApp stub: enviando a {} — {}", paciente.getNumeroWhatsApp(), mensaje);

        NotificacionWhatsAppEntity notificacion = new NotificacionWhatsAppEntity(
                UUID.randomUUID(), cita.getId(), paciente.getNumeroWhatsApp(),
                EstadoNotificacion.ENVIADA, 1, LocalDateTime.now(), mensaje);
        notificacionRepo.save(notificacion);
    }
}
