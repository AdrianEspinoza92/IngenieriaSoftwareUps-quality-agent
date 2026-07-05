package org.citasalud.infrastructure;

import org.citasalud.domain.model.*;
import org.citasalud.domain.port.in.ConsultarDisponibilidadUseCase;
import org.citasalud.infrastructure.web.controller.GlobalExceptionHandler;
import org.citasalud.infrastructure.web.controller.MedicosController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicosController.class)
@Import(GlobalExceptionHandler.class)
class MedicosControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private ConsultarDisponibilidadUseCase consultarDisponibilidadUseCase;

    private static final UUID MEDICO_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @Test
    void listarMedicos_retornaListaJson() throws Exception {
        when(consultarDisponibilidadUseCase.listarMedicos())
                .thenReturn(List.of(new Medico(MEDICO_ID, "Dr. Test", "Medicina General")));

        mockMvc.perform(get("/medicos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Dr. Test"))
                .andExpect(jsonPath("$[0].especialidad").value("Medicina General"));
    }

    @Test
    void listarMedicos_filtradoPorEspecialidad_retornaSoloCoincidentes() throws Exception {
        when(consultarDisponibilidadUseCase.listarMedicos())
                .thenReturn(List.of(
                        new Medico(MEDICO_ID, "Dr. Test", "Medicina General"),
                        new Medico(UUID.randomUUID(), "Dr. Otro", "Pediatría")
                ));

        mockMvc.perform(get("/medicos")
                        .param("especialidad", "Medicina General")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].especialidad").value("Medicina General"));
    }

    @Test
    void consultarDisponibilidad_retornaFranjasJson() throws Exception {
        FranjaHoraria franja = new FranjaHoraria(UUID.randomUUID(), MEDICO_ID, LocalDate.of(2026, 7, 15),
                LocalTime.of(9, 0), LocalTime.of(9, 30), EstadoFranja.DISPONIBLE);

        when(consultarDisponibilidadUseCase.consultarDisponibilidad(MEDICO_ID, LocalDate.of(2026, 7, 15)))
                .thenReturn(List.of(franja));

        mockMvc.perform(get("/medicos/{id}/disponibilidad", MEDICO_ID)
                        .param("fecha", "2026-07-15")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.franjas[0].estado").value("DISPONIBLE"));
    }
}
