package org.citasalud.infrastructure.persistence.mapper;

import org.citasalud.domain.model.Paciente;
import org.citasalud.infrastructure.persistence.entity.PacienteEntity;

public class PacienteMapper {

    public static Paciente toDomain(PacienteEntity entity) {
        return new Paciente(entity.getId(), entity.getNombre(), entity.getNumeroWhatsApp());
    }

    private PacienteMapper() {}
}
