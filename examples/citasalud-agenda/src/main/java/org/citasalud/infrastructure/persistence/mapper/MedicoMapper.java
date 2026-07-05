package org.citasalud.infrastructure.persistence.mapper;

import org.citasalud.domain.model.Medico;
import org.citasalud.infrastructure.persistence.entity.MedicoEntity;

public class MedicoMapper {

    public static Medico toDomain(MedicoEntity entity) {
        return new Medico(entity.getId(), entity.getNombre(), entity.getEspecialidad());
    }

    private MedicoMapper() {}
}
