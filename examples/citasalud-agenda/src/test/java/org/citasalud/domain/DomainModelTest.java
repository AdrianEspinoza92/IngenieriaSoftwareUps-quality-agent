package org.citasalud.domain;

import org.citasalud.domain.exception.FranjaOcupadaException;
import org.citasalud.domain.exception.RecursoNoEncontradoException;
import org.citasalud.domain.model.*;
import org.citasalud.infrastructure.persistence.mapper.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DomainModelTest {

    @Test
    void franjaHoraria_ocupar_cambiaEstado() {
        FranjaHoraria franja = new FranjaHoraria(UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(9, 30), EstadoFranja.DISPONIBLE);

        assertThat(franja.estaDisponible()).isTrue();
        franja.ocupar();
        assertThat(franja.estaDisponible()).isFalse();
        assertThat(franja.getEstado()).isEqualTo(EstadoFranja.OCUPADA);
    }

    @Test
    void cita_getters_funcionan() {
        UUID id = UUID.randomUUID();
        UUID pacienteId = UUID.randomUUID();
        UUID medicoId = UUID.randomUUID();
        UUID franjaId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Cita cita = new Cita(id, pacienteId, medicoId, franjaId, "CIT-2026-00001", EstadoCita.CONFIRMADA, now);

        assertThat(cita.getId()).isEqualTo(id);
        assertThat(cita.getPacienteId()).isEqualTo(pacienteId);
        assertThat(cita.getMedicoId()).isEqualTo(medicoId);
        assertThat(cita.getFranjaHorariaId()).isEqualTo(franjaId);
        assertThat(cita.getNumeroReferencia()).isEqualTo("CIT-2026-00001");
        assertThat(cita.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);
        assertThat(cita.getCreadaEn()).isEqualTo(now);
    }

    @Test
    void notificacionWhatsApp_getters_funcionan() {
        UUID id = UUID.randomUUID();
        UUID citaId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        NotificacionWhatsApp notif = new NotificacionWhatsApp(id, citaId, EstadoNotificacion.ENVIADA, 1, now);

        assertThat(notif.getId()).isEqualTo(id);
        assertThat(notif.getCitaId()).isEqualTo(citaId);
        assertThat(notif.getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        assertThat(notif.getIntentos()).isEqualTo(1);
        assertThat(notif.getUltimoIntento()).isEqualTo(now);
    }

    @Test
    void paciente_getters_funcionan() {
        UUID id = UUID.randomUUID();
        Paciente paciente = new Paciente(id, "Juan Test", "+593999000001");

        assertThat(paciente.getId()).isEqualTo(id);
        assertThat(paciente.getNombre()).isEqualTo("Juan Test");
        assertThat(paciente.getNumeroWhatsApp()).isEqualTo("+593999000001");
    }

    @Test
    void medico_getters_funcionan() {
        UUID id = UUID.randomUUID();
        Medico medico = new Medico(id, "Dr. Test", "Cardiología");

        assertThat(medico.getId()).isEqualTo(id);
        assertThat(medico.getNombre()).isEqualTo("Dr. Test");
        assertThat(medico.getEspecialidad()).isEqualTo("Cardiología");
    }

    @Test
    void franjaOcupadaException_getFranjaId_retornaId() {
        UUID franjaId = UUID.randomUUID();
        FranjaOcupadaException ex = new FranjaOcupadaException(franjaId);

        assertThat(ex.getFranjaId()).isEqualTo(franjaId);
        assertThat(ex.getMessage()).contains(franjaId.toString());
    }

    @Test
    void franjaHorariaMapper_toEntity_convierteCorrectamente() {
        FranjaHoraria franja = new FranjaHoraria(UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(9, 30), EstadoFranja.DISPONIBLE);
        var entity = FranjaHorariaMapper.toEntity(franja);
        assertThat(entity.getId()).isEqualTo(franja.getId());
        assertThat(entity.getEstado()).isEqualTo(franja.getEstado());
    }

    @Test
    void recursoNoEncontradoException_mensaje_correcto() {
        UUID id = UUID.randomUUID();
        RecursoNoEncontradoException ex = new RecursoNoEncontradoException("Medico", id);

        assertThat(ex.getMessage()).contains("Medico").contains(id.toString());
    }
}
