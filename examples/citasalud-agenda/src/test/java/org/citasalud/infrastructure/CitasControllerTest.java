package org.citasalud.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.citasalud.domain.exception.FranjaOcupadaException;
import org.citasalud.domain.exception.RecursoNoEncontradoException;
import org.citasalud.domain.model.*;

import java.util.List;
import org.citasalud.domain.port.in.ReservarCitaUseCase;
import org.citasalud.domain.port.out.FranjaHorariaRepositoryPort;
import org.citasalud.domain.port.out.MedicoRepositoryPort;
import org.citasalud.infrastructure.web.controller.CitasController;
import org.citasalud.infrastructure.web.controller.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CitasController.class)
@Import(GlobalExceptionHandler.class)
class CitasControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ReservarCitaUseCase reservarCitaUseCase;
    @MockBean private MedicoRepositoryPort medicoRepo;
    @MockBean private FranjaHorariaRepositoryPort franjaRepo;

    private static final UUID MEDICO_ID   = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID PACIENTE_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID FRANJA_ID   = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccc01");
    private static final UUID CITA_ID     = UUID.randomUUID();

    @Test
    void reservarCita_exitoso_retorna201() throws Exception {
        Cita cita = new Cita(CITA_ID, PACIENTE_ID, MEDICO_ID, FRANJA_ID,
                "CIT-2026-00001", EstadoCita.CONFIRMADA, LocalDateTime.now());
        Medico medico = new Medico(MEDICO_ID, "Dr. Test", "Medicina General");
        FranjaHoraria franja = new FranjaHoraria(FRANJA_ID, MEDICO_ID, LocalDate.of(2026, 7, 15),
                LocalTime.of(9, 0), LocalTime.of(9, 30), EstadoFranja.OCUPADA);

        when(reservarCitaUseCase.reservar(any(), any(), any(), any())).thenReturn(cita);
        when(medicoRepo.findById(MEDICO_ID)).thenReturn(Optional.of(medico));
        when(franjaRepo.findByIdForUpdate(FRANJA_ID)).thenReturn(Optional.of(franja));

        String body = """
                {
                    "pacienteId": "%s",
                    "medicoId": "%s",
                    "franjaHorariaId": "%s",
                    "fecha": "2026-07-15"
                }
                """.formatted(PACIENTE_ID, MEDICO_ID, FRANJA_ID);

        mockMvc.perform(post("/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroReferencia").value("CIT-2026-00001"))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    void reservarCita_franjaOcupada_retorna409ConAlternativas() throws Exception {
        UUID altId = UUID.randomUUID();
        FranjaHoraria alternativa = new FranjaHoraria(altId, MEDICO_ID,
                LocalDate.of(2026, 7, 15), LocalTime.of(9, 30), LocalTime.of(10, 0), EstadoFranja.DISPONIBLE);
        when(reservarCitaUseCase.reservar(any(), any(), any(), any()))
                .thenThrow(new FranjaOcupadaException(FRANJA_ID, List.of(alternativa)));

        String body = """
                {
                    "pacienteId": "%s",
                    "medicoId": "%s",
                    "franjaHorariaId": "%s",
                    "fecha": "2026-07-15"
                }
                """.formatted(PACIENTE_ID, MEDICO_ID, FRANJA_ID);

        mockMvc.perform(post("/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("FRANJA_OCUPADA"))
                .andExpect(jsonPath("$.franjasAlternativas").isArray())
                .andExpect(jsonPath("$.franjasAlternativas[0].estado").value("DISPONIBLE"));
    }

    @Test
    void consultarCita_retornaCita() throws Exception {
        Cita cita = new Cita(CITA_ID, PACIENTE_ID, MEDICO_ID, FRANJA_ID,
                "CIT-2026-00001", EstadoCita.CONFIRMADA, LocalDateTime.now());
        Medico medico = new Medico(MEDICO_ID, "Dr. Test", "Medicina General");
        FranjaHoraria franja = new FranjaHoraria(FRANJA_ID, MEDICO_ID, LocalDate.of(2026, 7, 15),
                LocalTime.of(9, 0), LocalTime.of(9, 30), EstadoFranja.OCUPADA);

        when(reservarCitaUseCase.buscarPorId(CITA_ID)).thenReturn(cita);
        when(medicoRepo.findById(MEDICO_ID)).thenReturn(Optional.of(medico));
        when(franjaRepo.findByIdForUpdate(FRANJA_ID)).thenReturn(Optional.of(franja));

        mockMvc.perform(get("/citas/{id}", CITA_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroReferencia").value("CIT-2026-00001"));
    }

    @Test
    void reservarCita_optimisticLock_retorna409() throws Exception {
        when(reservarCitaUseCase.reservar(any(), any(), any(), any()))
                .thenThrow(new org.springframework.orm.ObjectOptimisticLockingFailureException("FranjaHoraria", FRANJA_ID));

        String body = """
                {
                    "pacienteId": "%s",
                    "medicoId": "%s",
                    "franjaHorariaId": "%s",
                    "fecha": "2026-07-15"
                }
                """.formatted(PACIENTE_ID, MEDICO_ID, FRANJA_ID);

        mockMvc.perform(post("/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("FRANJA_OCUPADA"));
    }

    @Test
    void reservarCita_medicoNoEncontrado_retorna404() throws Exception {
        when(reservarCitaUseCase.reservar(any(), any(), any(), any()))
                .thenThrow(new RecursoNoEncontradoException("Medico", MEDICO_ID));

        String body = """
                {
                    "pacienteId": "%s",
                    "medicoId": "%s",
                    "franjaHorariaId": "%s",
                    "fecha": "2026-07-15"
                }
                """.formatted(PACIENTE_ID, MEDICO_ID, FRANJA_ID);

        mockMvc.perform(post("/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("RECURSO_NO_ENCONTRADO"));
    }

    @Test
    void reservarCita_errorGenerico_retorna500() throws Exception {
        when(reservarCitaUseCase.reservar(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("error inesperado"));

        String body = """
                {
                    "pacienteId": "%s",
                    "medicoId": "%s",
                    "franjaHorariaId": "%s",
                    "fecha": "2026-07-15"
                }
                """.formatted(PACIENTE_ID, MEDICO_ID, FRANJA_ID);

        mockMvc.perform(post("/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value("ERROR_INTERNO"));
    }

    @Test
    void reservarCita_exitoso_medicoNoEncontradoEnController_retorna404() throws Exception {
        Cita cita = new Cita(CITA_ID, PACIENTE_ID, MEDICO_ID, FRANJA_ID,
                "CIT-2026-00001", EstadoCita.CONFIRMADA, LocalDateTime.now());
        when(reservarCitaUseCase.reservar(any(), any(), any(), any())).thenReturn(cita);
        when(medicoRepo.findById(any())).thenReturn(Optional.empty());

        String body = """
                {
                    "pacienteId": "%s",
                    "medicoId": "%s",
                    "franjaHorariaId": "%s",
                    "fecha": "2026-07-15"
                }
                """.formatted(PACIENTE_ID, MEDICO_ID, FRANJA_ID);

        mockMvc.perform(post("/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void consultarCita_noEncontrada_retorna404() throws Exception {
        when(reservarCitaUseCase.buscarPorId(any()))
                .thenThrow(new RecursoNoEncontradoException("Cita", CITA_ID));

        mockMvc.perform(get("/citas/{id}", CITA_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
