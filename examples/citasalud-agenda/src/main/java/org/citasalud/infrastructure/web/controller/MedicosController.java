package org.citasalud.infrastructure.web.controller;

import org.citasalud.api.MedicosApi;
import org.citasalud.api.model.DisponibilidadResponse;
import org.citasalud.api.model.MedicoResponse;
import org.citasalud.domain.port.in.ConsultarDisponibilidadUseCase;
import org.citasalud.infrastructure.web.mapper.ApiMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
public class MedicosController implements MedicosApi {

    private final ConsultarDisponibilidadUseCase consultarDisponibilidadUseCase;

    public MedicosController(ConsultarDisponibilidadUseCase consultarDisponibilidadUseCase) {
        this.consultarDisponibilidadUseCase = consultarDisponibilidadUseCase;
    }

    @Override
    public ResponseEntity<List<MedicoResponse>> listarMedicos(String especialidad) {
        List<MedicoResponse> medicos = consultarDisponibilidadUseCase.listarMedicos().stream()
                .filter(m -> especialidad == null || m.getEspecialidad().toLowerCase().contains(especialidad.toLowerCase()))
                .map(ApiMapper::toMedicoResponse)
                .toList();
        return ResponseEntity.ok(medicos);
    }

    @Override
    public ResponseEntity<DisponibilidadResponse> consultarDisponibilidad(UUID medicoId, LocalDate fecha) {
        var franjas = consultarDisponibilidadUseCase.consultarDisponibilidad(medicoId, fecha);
        return ResponseEntity.ok(ApiMapper.toDisponibilidadResponse(medicoId, fecha, franjas));
    }
}
