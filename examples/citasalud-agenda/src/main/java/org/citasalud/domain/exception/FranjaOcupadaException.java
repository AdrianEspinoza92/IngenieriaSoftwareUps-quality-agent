package org.citasalud.domain.exception;

import org.citasalud.domain.model.FranjaHoraria;

import java.util.List;
import java.util.UUID;

public class FranjaOcupadaException extends RuntimeException {

    private final UUID franjaId;
    private final List<FranjaHoraria> alternativas;

    public FranjaOcupadaException(UUID franjaId) {
        this(franjaId, List.of());
    }

    public FranjaOcupadaException(UUID franjaId, List<FranjaHoraria> alternativas) {
        super("La franja horaria " + franjaId + " no está disponible");
        this.franjaId = franjaId;
        this.alternativas = alternativas != null ? alternativas : List.of();
    }

    public UUID getFranjaId() { return franjaId; }
    public List<FranjaHoraria> getAlternativas() { return alternativas; }
}
