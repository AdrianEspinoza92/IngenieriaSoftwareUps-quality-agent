package org.citasalud.infrastructure;

import org.citasalud.domain.model.Cita;
import org.citasalud.domain.model.EstadoCita;
import org.citasalud.domain.model.EstadoFranja;
import org.citasalud.domain.port.in.ReservarCitaUseCase;
import org.citasalud.infrastructure.persistence.repository.FranjaHorariaJpaRepository;
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
class ReservarCitaIntegrationTest {

    @Autowired
    private ReservarCitaUseCase reservarCitaUseCase;

    @Autowired
    private FranjaHorariaJpaRepository franjaRepo;

    private static final UUID MEDICO_ID   = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID PACIENTE_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID FRANJA_ID   = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccc02");

    @Test
    void reservar_persisteCitaYActualizaFranja() {
        // When
        Cita cita = reservarCitaUseCase.reservar(PACIENTE_ID, MEDICO_ID, FRANJA_ID, LocalDate.of(2026, 7, 15));

        // Then
        assertThat(cita.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);
        assertThat(cita.getNumeroReferencia()).startsWith("CIT-");

        var franjaActualizada = franjaRepo.findById(FRANJA_ID).orElseThrow();
        assertThat(franjaActualizada.getEstado()).isEqualTo(EstadoFranja.OCUPADA);
    }
}
