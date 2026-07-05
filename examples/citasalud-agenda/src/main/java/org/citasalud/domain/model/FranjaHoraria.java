package org.citasalud.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class FranjaHoraria {

    private final UUID id;
    private final UUID medicoId;
    private final LocalDate fecha;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private EstadoFranja estado;

    public FranjaHoraria(UUID id, UUID medicoId, LocalDate fecha,
                         LocalTime horaInicio, LocalTime horaFin, EstadoFranja estado) {
        this.id = id;
        this.medicoId = medicoId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
    }

    public boolean estaDisponible() {
        return EstadoFranja.DISPONIBLE.equals(estado);
    }

    public void ocupar() {
        this.estado = EstadoFranja.OCUPADA;
    }

    public UUID getId() { return id; }
    public UUID getMedicoId() { return medicoId; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public EstadoFranja getEstado() { return estado; }
}
