package org.citasalud.application;

import org.citasalud.application.usecase.ReservarCitaUseCaseImpl;
import org.citasalud.domain.model.*;
import org.citasalud.domain.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ReservarCitaUseCaseTest {

    @Mock private FranjaHorariaRepositoryPort franjaRepo;
    @Mock private CitaRepositoryPort citaRepo;
    @Mock private MedicoRepositoryPort medicoRepo;
    @Mock private PacienteRepositoryPort pacienteRepo;
    @Mock private NotificacionWhatsAppPort notificacionPort;

    private ReservarCitaUseCaseImpl useCase;

    private static final UUID MEDICO_ID   = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID PACIENTE_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID FRANJA_ID   = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccc01");

    @BeforeEach
    void setUp() {
        useCase = new ReservarCitaUseCaseImpl(franjaRepo, citaRepo, medicoRepo, pacienteRepo, notificacionPort);
    }

    @Test
    void reservar_franjaDisponible_citaConfirmada() {
        // Given
        FranjaHoraria franja = franjaDisponible();
        Medico medico = new Medico(MEDICO_ID, "Dr. Test", "General");
        Paciente paciente = new Paciente(PACIENTE_ID, "Paciente Test", "+593999000001");

        when(franjaRepo.findByIdForUpdate(FRANJA_ID)).thenReturn(Optional.of(franja));
        when(medicoRepo.findById(MEDICO_ID)).thenReturn(Optional.of(medico));
        when(pacienteRepo.findById(PACIENTE_ID)).thenReturn(Optional.of(paciente));
        when(citaRepo.countAll()).thenReturn(0L);
        when(citaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(franjaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        Cita cita = useCase.reservar(PACIENTE_ID, MEDICO_ID, FRANJA_ID, LocalDate.of(2026, 7, 15));

        // Then
        assertThat(cita.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);
        assertThat(cita.getNumeroReferencia()).startsWith("CIT-");
        assertThat(franja.getEstado()).isEqualTo(EstadoFranja.OCUPADA);
        verify(notificacionPort).enviarConfirmacion(any(), eq(medico), eq(franja), eq(paciente));
    }

    @Test
    void reservar_franjaOcupada_lanzaExcepcionConAlternativas() {
        // Given
        FranjaHoraria franjaOcupada = new FranjaHoraria(FRANJA_ID, MEDICO_ID,
                LocalDate.of(2026, 7, 15), LocalTime.of(9, 0), LocalTime.of(9, 30), EstadoFranja.OCUPADA);
        FranjaHoraria franjaAlternativa = new FranjaHoraria(UUID.randomUUID(), MEDICO_ID,
                LocalDate.of(2026, 7, 15), LocalTime.of(9, 30), LocalTime.of(10, 0), EstadoFranja.DISPONIBLE);

        when(franjaRepo.findByIdForUpdate(FRANJA_ID)).thenReturn(Optional.of(franjaOcupada));
        when(franjaRepo.findByMedicoIdAndFecha(MEDICO_ID, LocalDate.of(2026, 7, 15)))
                .thenReturn(List.of(franjaOcupada, franjaAlternativa));

        // When / Then
        org.citasalud.domain.exception.FranjaOcupadaException ex = (org.citasalud.domain.exception.FranjaOcupadaException)
                org.assertj.core.api.Assertions.catchThrowable(
                        () -> useCase.reservar(PACIENTE_ID, MEDICO_ID, FRANJA_ID, LocalDate.of(2026, 7, 15)));

        assertThat(ex).isInstanceOf(org.citasalud.domain.exception.FranjaOcupadaException.class);
        assertThat(ex.getAlternativas()).hasSize(2);
        verify(citaRepo, never()).save(any());
    }

    @Test
    void reservar_whatsappFalla_citaQuedarGuardada() {
        // Given
        FranjaHoraria franja = franjaDisponible();
        Medico medico = new Medico(MEDICO_ID, "Dr. Test", "General");
        Paciente paciente = new Paciente(PACIENTE_ID, "Paciente Test", "+593999000001");

        when(franjaRepo.findByIdForUpdate(FRANJA_ID)).thenReturn(Optional.of(franja));
        when(medicoRepo.findById(MEDICO_ID)).thenReturn(Optional.of(medico));
        when(pacienteRepo.findById(PACIENTE_ID)).thenReturn(Optional.of(paciente));
        when(citaRepo.countAll()).thenReturn(0L);
        when(citaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(franjaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new RuntimeException("WhatsApp no disponible")).when(notificacionPort)
                .enviarConfirmacion(any(), any(), any(), any());

        // When — no debe lanzar excepción
        Cita cita = useCase.reservar(PACIENTE_ID, MEDICO_ID, FRANJA_ID, LocalDate.of(2026, 7, 15));

        // Then — la cita se guardó aunque WhatsApp falló
        assertThat(cita.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);
        verify(citaRepo).save(any());
    }

    @Test
    void reservar_franjaNoExiste_lanzaExcepcion() {
        when(franjaRepo.findByIdForUpdate(FRANJA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.reservar(PACIENTE_ID, MEDICO_ID, FRANJA_ID, LocalDate.of(2026, 7, 15)))
                .isInstanceOf(org.citasalud.domain.exception.RecursoNoEncontradoException.class);
    }

    private FranjaHoraria franjaDisponible() {
        return new FranjaHoraria(FRANJA_ID, MEDICO_ID,
                LocalDate.of(2026, 7, 15), LocalTime.of(9, 0), LocalTime.of(9, 30), EstadoFranja.DISPONIBLE);
    }
}
