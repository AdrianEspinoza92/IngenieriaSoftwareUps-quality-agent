package org.citasalud.infrastructure.web.mapper;

import org.citasalud.api.model.*;
import org.citasalud.domain.model.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class ApiMapper {

    public static MedicoResponse toMedicoResponse(Medico medico) {
        MedicoResponse r = new MedicoResponse();
        r.setId(medico.getId());
        r.setNombre(medico.getNombre());
        r.setEspecialidad(medico.getEspecialidad());
        return r;
    }

    public static FranjaHorariaResponse toFranjaResponse(FranjaHoraria franja) {
        FranjaHorariaResponse r = new FranjaHorariaResponse();
        r.setId(franja.getId());
        r.setHoraInicio(franja.getHoraInicio().toString());
        r.setHoraFin(franja.getHoraFin().toString());
        r.setEstado(FranjaHorariaResponse.EstadoEnum.valueOf(franja.getEstado().name()));
        return r;
    }

    public static DisponibilidadResponse toDisponibilidadResponse(java.util.UUID medicoId,
                                                                    java.time.LocalDate fecha,
                                                                    List<FranjaHoraria> franjas) {
        DisponibilidadResponse r = new DisponibilidadResponse();
        r.setMedicoId(medicoId);
        r.setFecha(fecha);
        r.setFranjas(franjas.stream().map(ApiMapper::toFranjaResponse).toList());
        return r;
    }

    public static CitaResponse toCitaResponse(Cita cita, Medico medico, FranjaHoraria franja) {
        CitaResponse r = new CitaResponse();
        r.setId(cita.getId());
        r.setPacienteId(cita.getPacienteId());
        r.setMedicoNombre(medico.getNombre());
        r.setEspecialidad(medico.getEspecialidad());
        r.setFecha(franja.getFecha());
        r.setHoraInicio(franja.getHoraInicio().toString());
        r.setHoraFin(franja.getHoraFin().toString());
        r.setNumeroReferencia(cita.getNumeroReferencia());
        r.setEstado(CitaResponse.EstadoEnum.valueOf(cita.getEstado().name()));
        r.setCreadaEn(cita.getCreadaEn().atOffset(ZoneOffset.UTC));
        return r;
    }

    private ApiMapper() {}
}
