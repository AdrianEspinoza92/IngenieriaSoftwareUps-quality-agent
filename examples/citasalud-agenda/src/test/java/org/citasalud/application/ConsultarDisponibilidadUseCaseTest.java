package org.citasalud.application;

import org.citasalud.application.usecase.ConsultarDisponibilidadUseCaseImpl;
import org.citasalud.domain.model.*;
import org.citasalud.domain.port.out.FranjaHorariaRepositoryPort;
import org.citasalud.domain.port.out.MedicoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarDisponibilidadUseCaseTest {

    @Mock private MedicoRepositoryPort medicoRepo;
    @Mock private FranjaHorariaRepositoryPort franjaRepo;

    private ConsultarDisponibilidadUseCaseImpl useCase;

    private static final UUID MEDICO_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @BeforeEach
    void setUp() {
        useCase = new ConsultarDisponibilidadUseCaseImpl(medicoRepo, franjaRepo);
    }

    @Test
    void listarMedicos_retornaListaDeMedicos() {
        List<Medico> medicos = List.of(
                new Medico(MEDICO_ID, "Dr. Test", "Medicina General"),
                new Medico(UUID.randomUUID(), "Dra. Test2", "Cardiología")
        );
        when(medicoRepo.findAll()).thenReturn(medicos);

        List<Medico> result = useCase.listarMedicos();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNombre()).isEqualTo("Dr. Test");
    }

    @Test
    void consultarDisponibilidad_retornaFranjas() {
        LocalDate fecha = LocalDate.of(2026, 7, 15);
        List<FranjaHoraria> franjas = List.of(
                new FranjaHoraria(UUID.randomUUID(), MEDICO_ID, fecha,
                        LocalTime.of(9, 0), LocalTime.of(9, 30), EstadoFranja.DISPONIBLE),
                new FranjaHoraria(UUID.randomUUID(), MEDICO_ID, fecha,
                        LocalTime.of(9, 30), LocalTime.of(10, 0), EstadoFranja.OCUPADA)
        );
        when(franjaRepo.findByMedicoIdAndFecha(MEDICO_ID, fecha)).thenReturn(franjas);

        List<FranjaHoraria> result = useCase.consultarDisponibilidad(MEDICO_ID, fecha);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).estaDisponible()).isTrue();
        assertThat(result.get(1).estaDisponible()).isFalse();
    }

    @Test
    void consultarDisponibilidad_sinFranjas_retornaListaVacia() {
        LocalDate fecha = LocalDate.of(2099, 1, 1);
        when(franjaRepo.findByMedicoIdAndFecha(MEDICO_ID, fecha)).thenReturn(List.of());

        List<FranjaHoraria> result = useCase.consultarDisponibilidad(MEDICO_ID, fecha);

        assertThat(result).isEmpty();
    }
}
