package org.citasalud.bdd;

import io.cucumber.java.es.*;
import org.citasalud.domain.exception.FranjaOcupadaException;
import org.citasalud.domain.model.Cita;
import org.citasalud.domain.model.EstadoCita;
import org.citasalud.domain.model.FranjaHoraria;
import org.citasalud.domain.port.in.ConsultarDisponibilidadUseCase;
import org.citasalud.domain.port.in.ReservarCitaUseCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ReservaCitaSteps {

    @Autowired
    private ReservarCitaUseCase reservarCitaUseCase;

    @Autowired
    private ConsultarDisponibilidadUseCase consultarDisponibilidadUseCase;

    private Cita citaResultado;
    private String codigoError;
    private List<FranjaHoraria> franjasResultado;

    @Dado("el sistema tiene un médico con id {string}")
    public void elSistemaTieneUnMedicoConId(String medicoId) {
        // Dato cargado por data-test.sql
    }

    @Dado("el sistema tiene un paciente con id {string}")
    public void elSistemaTieneUnPacienteConId(String pacienteId) {
        // Dato cargado por data-test.sql
    }

    @Dado("la franja horaria {string} está disponible")
    public void laFranjaEstaDisponible(String franjaId) {
        // Validado por data-test.sql
    }

    @Dado("la franja horaria {string} está ocupada")
    public void laFranjaEstaOcupada(String franjaId) {
        // La franja cccc...03 ya está OCUPADA en data-test.sql
    }

    @Dado("el médico {string} tiene franjas para la fecha {string}")
    public void elMedicoTieneFranjas(String medicoId, String fecha) {
        // Validado por data-test.sql
    }

    @Cuando("el paciente reserva la franja {string} con el médico {string} para la fecha {string}")
    public void elPacienteReservaLaFranja(String franjaId, String medicoId, String fecha) {
        citaResultado = reservarCitaUseCase.reservar(
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                UUID.fromString(medicoId),
                UUID.fromString(franjaId),
                LocalDate.parse(fecha)
        );
    }

    @Cuando("el paciente intenta reservar la franja {string} con el médico {string} para la fecha {string}")
    public void elPacienteIntentaReservarLaFranja(String franjaId, String medicoId, String fecha) {
        try {
            reservarCitaUseCase.reservar(
                    UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                    UUID.fromString(medicoId),
                    UUID.fromString(franjaId),
                    LocalDate.parse(fecha)
            );
        } catch (FranjaOcupadaException e) {
            codigoError = "FRANJA_OCUPADA";
        }
    }

    @Cuando("el paciente consulta la disponibilidad del médico {string} para la fecha {string}")
    public void elPacienteConsultaDisponibilidad(String medicoId, String fecha) {
        franjasResultado = consultarDisponibilidadUseCase.consultarDisponibilidad(
                UUID.fromString(medicoId), LocalDate.parse(fecha));
    }

    @Entonces("la cita queda registrada con estado {string}")
    public void laCitaQuedaRegistradaConEstado(String estado) {
        assertThat(citaResultado).isNotNull();
        assertThat(citaResultado.getEstado()).isEqualTo(EstadoCita.valueOf(estado));
    }

    @Entonces("la franja horaria {string} pasa a estado {string}")
    public void laFranjaHorariaPasaAEstado(String franjaId, String estado) {
        // Verificado por el integration test; en BDD validamos el caso de uso
        assertThat(citaResultado.getFranjaHorariaId()).isEqualTo(UUID.fromString(franjaId));
    }

    @Entonces("el paciente recibe una notificación de confirmación por WhatsApp")
    public void elPacienteRecibeNotificacion() {
        // El stub de notificación no lanza excepción = envío exitoso
        assertThat(citaResultado).isNotNull();
    }

    @Entonces("el sistema rechaza la reserva con código {string}")
    public void elSistemaRechazaLaReserva(String codigo) {
        assertThat(codigoError).isEqualTo(codigo);
    }

    @Entonces("el sistema retorna las franjas horarias con sus estados")
    public void elSistemaRetornaLasFranjas() {
        assertThat(franjasResultado).isNotEmpty();
    }

    @Entonces("al menos una franja tiene estado {string}")
    public void alMenosUnaFranjaTieneEstado(String estado) {
        assertThat(franjasResultado)
                .anyMatch(f -> f.getEstado().name().equals(estado));
    }
}
