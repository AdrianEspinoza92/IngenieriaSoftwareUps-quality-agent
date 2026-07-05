package org.citasalud.infrastructure;

import org.citasalud.domain.model.Cita;
import org.citasalud.domain.model.EstadoCita;
import org.citasalud.domain.port.in.ReservarCitaUseCase;
import org.citasalud.infrastructure.persistence.repository.NotificacionWhatsAppJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
class WhatsAppNotificationTest {

    @Autowired
    private ReservarCitaUseCase reservarCitaUseCase;

    @Autowired
    private NotificacionWhatsAppJpaRepository notificacionRepo;

    private static final UUID MEDICO_ID   = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID PACIENTE_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID FRANJA_ID   = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccc05");
    private static final LocalDate FECHA  = LocalDate.of(2026, 7, 15);

    @Test
    void enviarConfirmacion_mensajeContieneCincoElementos() {
        Cita cita = reservarCitaUseCase.reservar(PACIENTE_ID, MEDICO_ID, FRANJA_ID, FECHA);

        assertThat(cita.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);

        var notificaciones = notificacionRepo.findByCitaId(cita.getId());
        assertThat(notificaciones).isNotEmpty();

        String mensaje = notificaciones.get(0).getMensaje();
        assertThat(mensaje).isNotBlank();
        // Verificar los 5 campos obligatorios: nombre médico, especialidad, fecha, hora, referencia
        assertThat(mensaje).contains("Dr. Test Médico");
        assertThat(mensaje).contains("Medicina General");
        assertThat(mensaje).contains("2026-07-15");
        assertThat(mensaje).contains("11:00");
        assertThat(mensaje).contains(cita.getNumeroReferencia());

        String destinatario = notificaciones.get(0).getDestinatario();
        assertThat(destinatario).isEqualTo("+593999000001");
    }
}
