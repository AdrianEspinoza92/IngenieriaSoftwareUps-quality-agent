package org.citasalud.application.usecase;

import org.citasalud.domain.exception.FranjaOcupadaException;
import org.citasalud.domain.exception.RecursoNoEncontradoException;
import org.citasalud.domain.model.*;
import org.citasalud.domain.port.in.ReservarCitaUseCase;
import org.citasalud.domain.port.out.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ReservarCitaUseCaseImpl implements ReservarCitaUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReservarCitaUseCaseImpl.class);

    private final FranjaHorariaRepositoryPort franjaRepo;
    private final CitaRepositoryPort citaRepo;
    private final MedicoRepositoryPort medicoRepo;
    private final PacienteRepositoryPort pacienteRepo;
    private final NotificacionWhatsAppPort notificacionPort;

    public ReservarCitaUseCaseImpl(FranjaHorariaRepositoryPort franjaRepo,
                                    CitaRepositoryPort citaRepo,
                                    MedicoRepositoryPort medicoRepo,
                                    PacienteRepositoryPort pacienteRepo,
                                    NotificacionWhatsAppPort notificacionPort) {
        this.franjaRepo = franjaRepo;
        this.citaRepo = citaRepo;
        this.medicoRepo = medicoRepo;
        this.pacienteRepo = pacienteRepo;
        this.notificacionPort = notificacionPort;
    }

    @Override
    @Transactional
    public Cita reservar(UUID pacienteId, UUID medicoId, UUID franjaHorariaId, LocalDate fecha) {
        FranjaHoraria franja = franjaRepo.findByIdForUpdate(franjaHorariaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("FranjaHoraria", franjaHorariaId));

        if (!franja.estaDisponible()) {
            List<FranjaHoraria> alternativas = franjaRepo.findByMedicoIdAndFecha(franja.getMedicoId(), franja.getFecha());
            throw new FranjaOcupadaException(franjaHorariaId, alternativas);
        }

        Medico medico = medicoRepo.findById(medicoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medico", medicoId));

        Paciente paciente = pacienteRepo.findById(pacienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente", pacienteId));

        franja.ocupar();
        franjaRepo.save(franja);

        String numeroReferencia = generarNumeroReferencia(fecha);
        Cita cita = new Cita(UUID.randomUUID(), pacienteId, medicoId, franjaHorariaId,
                numeroReferencia, EstadoCita.CONFIRMADA, LocalDateTime.now());
        Cita citaGuardada = citaRepo.save(cita);

        try {
            notificacionPort.enviarConfirmacion(citaGuardada, medico, franja, paciente);
        } catch (Exception e) {
            log.warn("Notificación WhatsApp falló para cita {}: {}", citaGuardada.getId(), e.getMessage());
        }

        return citaGuardada;
    }

    @Override
    public Cita buscarPorId(UUID citaId) {
        return citaRepo.findById(citaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita", citaId));
    }

    private String generarNumeroReferencia(LocalDate fecha) {
        long secuencial = citaRepo.countAll() + 1;
        return String.format("CIT-%d-%05d", fecha.getYear(), secuencial);
    }
}
