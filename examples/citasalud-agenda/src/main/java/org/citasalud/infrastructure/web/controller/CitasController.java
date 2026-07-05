package org.citasalud.infrastructure.web.controller;

import org.citasalud.api.CitasApi;
import org.citasalud.api.model.CitaResponse;
import org.citasalud.api.model.ReservarCitaRequest;
import org.citasalud.domain.exception.RecursoNoEncontradoException;
import org.citasalud.domain.model.Cita;
import org.citasalud.domain.model.FranjaHoraria;
import org.citasalud.domain.model.Medico;
import org.citasalud.domain.port.in.ReservarCitaUseCase;
import org.citasalud.domain.port.out.FranjaHorariaRepositoryPort;
import org.citasalud.domain.port.out.MedicoRepositoryPort;
import org.citasalud.infrastructure.web.mapper.ApiMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
public class CitasController implements CitasApi {

    private final ReservarCitaUseCase reservarCitaUseCase;
    private final MedicoRepositoryPort medicoRepo;
    private final FranjaHorariaRepositoryPort franjaRepo;

    public CitasController(ReservarCitaUseCase reservarCitaUseCase,
                           MedicoRepositoryPort medicoRepo,
                           FranjaHorariaRepositoryPort franjaRepo) {
        this.reservarCitaUseCase = reservarCitaUseCase;
        this.medicoRepo = medicoRepo;
        this.franjaRepo = franjaRepo;
    }

    @Override
    public ResponseEntity<CitaResponse> reservarCita(ReservarCitaRequest request) {
        Cita cita = reservarCitaUseCase.reservar(
                request.getPacienteId(),
                request.getMedicoId(),
                request.getFranjaHorariaId(),
                request.getFecha()
        );
        Medico medico = medicoRepo.findById(cita.getMedicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Medico", cita.getMedicoId()));
        FranjaHoraria franja = franjaRepo.findByIdForUpdate(cita.getFranjaHorariaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("FranjaHoraria", cita.getFranjaHorariaId()));

        return ResponseEntity
                .created(URI.create("/api/v1/citas/" + cita.getId()))
                .body(ApiMapper.toCitaResponse(cita, medico, franja));
    }

    @Override
    public ResponseEntity<CitaResponse> consultarCita(UUID citaId) {
        Cita cita = reservarCitaUseCase.buscarPorId(citaId);
        Medico medico = medicoRepo.findById(cita.getMedicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Medico", cita.getMedicoId()));
        FranjaHoraria franja = franjaRepo.findByIdForUpdate(cita.getFranjaHorariaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("FranjaHoraria", cita.getFranjaHorariaId()));
        return ResponseEntity.ok(ApiMapper.toCitaResponse(cita, medico, franja));
    }
}
