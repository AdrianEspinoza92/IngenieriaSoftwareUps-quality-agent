package org.citasalud.infrastructure;

import org.citasalud.domain.model.*;
import org.citasalud.domain.port.out.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
class PersistenceAdapterTest {

    @Autowired private MedicoRepositoryPort medicoRepo;
    @Autowired private PacienteRepositoryPort pacienteRepo;
    @Autowired private FranjaHorariaRepositoryPort franjaRepo;
    @Autowired private CitaRepositoryPort citaRepo;

    private static final UUID MEDICO_ID   = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID PACIENTE_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    @Test
    void medicoRepo_findAll_retornaMedicos() {
        List<Medico> medicos = medicoRepo.findAll();
        assertThat(medicos).isNotEmpty();
    }

    @Test
    void medicoRepo_findById_retornaMedico() {
        Optional<Medico> medico = medicoRepo.findById(MEDICO_ID);
        assertThat(medico).isPresent();
        assertThat(medico.get().getNombre()).isEqualTo("Dr. Test Médico");
    }

    @Test
    void medicoRepo_findById_noExiste_retornaVacio() {
        Optional<Medico> medico = medicoRepo.findById(UUID.randomUUID());
        assertThat(medico).isEmpty();
    }

    @Test
    void pacienteRepo_findById_retornaPaciente() {
        Optional<Paciente> paciente = pacienteRepo.findById(PACIENTE_ID);
        assertThat(paciente).isPresent();
        assertThat(paciente.get().getNumeroWhatsApp()).isEqualTo("+593999000001");
    }

    @Test
    void franjaRepo_findByMedicoIdAndFecha_retornaFranjas() {
        List<FranjaHoraria> franjas = franjaRepo.findByMedicoIdAndFecha(MEDICO_ID, LocalDate.of(2026, 7, 15));
        assertThat(franjas).hasSize(5);
    }

    @Test
    void citaRepo_findById_noExiste_retornaVacio() {
        Optional<Cita> cita = citaRepo.findById(UUID.randomUUID());
        assertThat(cita).isEmpty();
    }

    @Test
    void citaRepo_countAll_retornaConteo() {
        long count = citaRepo.countAll();
        assertThat(count).isGreaterThanOrEqualTo(0);
    }
}
