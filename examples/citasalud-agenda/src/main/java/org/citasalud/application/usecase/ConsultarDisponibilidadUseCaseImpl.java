package org.citasalud.application.usecase;

import org.citasalud.domain.model.FranjaHoraria;
import org.citasalud.domain.model.Medico;
import org.citasalud.domain.port.in.ConsultarDisponibilidadUseCase;
import org.citasalud.domain.port.out.FranjaHorariaRepositoryPort;
import org.citasalud.domain.port.out.MedicoRepositoryPort;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ConsultarDisponibilidadUseCaseImpl implements ConsultarDisponibilidadUseCase {

    private final MedicoRepositoryPort medicoRepo;
    private final FranjaHorariaRepositoryPort franjaRepo;

    public ConsultarDisponibilidadUseCaseImpl(MedicoRepositoryPort medicoRepo,
                                               FranjaHorariaRepositoryPort franjaRepo) {
        this.medicoRepo = medicoRepo;
        this.franjaRepo = franjaRepo;
    }

    @Override
    public List<Medico> listarMedicos() {
        return medicoRepo.findAll();
    }

    @Override
    public List<FranjaHoraria> consultarDisponibilidad(UUID medicoId, LocalDate fecha) {
        return franjaRepo.findByMedicoIdAndFecha(medicoId, fecha);
    }
}
