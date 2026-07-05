package org.citasalud.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Cita {

    private final UUID id;
    private final UUID pacienteId;
    private final UUID medicoId;
    private final UUID franjaHorariaId;
    private final String numeroReferencia;
    private EstadoCita estado;
    private final LocalDateTime creadaEn;

    public Cita(UUID id, UUID pacienteId, UUID medicoId, UUID franjaHorariaId,
                String numeroReferencia, EstadoCita estado, LocalDateTime creadaEn) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.franjaHorariaId = franjaHorariaId;
        this.numeroReferencia = numeroReferencia;
        this.estado = estado;
        this.creadaEn = creadaEn;
    }

    public UUID getId() { return id; }
    public UUID getPacienteId() { return pacienteId; }
    public UUID getMedicoId() { return medicoId; }
    public UUID getFranjaHorariaId() { return franjaHorariaId; }
    public String getNumeroReferencia() { return numeroReferencia; }
    public EstadoCita getEstado() { return estado; }
    public LocalDateTime getCreadaEn() { return creadaEn; }
}
