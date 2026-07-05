package org.citasalud.infrastructure.persistence.mapper;

import org.citasalud.domain.model.FranjaHoraria;
import org.citasalud.infrastructure.persistence.entity.FranjaHorariaEntity;

public class FranjaHorariaMapper {

    public static FranjaHoraria toDomain(FranjaHorariaEntity entity) {
        return new FranjaHoraria(entity.getId(), entity.getMedicoId(), entity.getFecha(),
                entity.getHoraInicio(), entity.getHoraFin(), entity.getEstado());
    }

    public static FranjaHorariaEntity toEntity(FranjaHoraria domain) {
        return new FranjaHorariaEntity(domain.getId(), domain.getMedicoId(), domain.getFecha(),
                domain.getHoraInicio(), domain.getHoraFin(), domain.getEstado());
    }

    private FranjaHorariaMapper() {}
}
