package org.citasalud.infrastructure.persistence.mapper;

import org.citasalud.domain.model.Cita;
import org.citasalud.infrastructure.persistence.entity.CitaEntity;

public class CitaMapper {

    public static Cita toDomain(CitaEntity entity) {
        return new Cita(entity.getId(), entity.getPacienteId(), entity.getMedicoId(),
                entity.getFranjaHorariaId(), entity.getNumeroReferencia(),
                entity.getEstado(), entity.getCreadaEn());
    }

    public static CitaEntity toEntity(Cita domain) {
        return new CitaEntity(domain.getId(), domain.getPacienteId(), domain.getMedicoId(),
                domain.getFranjaHorariaId(), domain.getNumeroReferencia(),
                domain.getEstado(), domain.getCreadaEn());
    }

    private CitaMapper() {}
}
