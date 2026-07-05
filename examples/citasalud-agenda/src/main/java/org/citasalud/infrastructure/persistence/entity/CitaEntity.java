package org.citasalud.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.citasalud.domain.model.EstadoCita;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "citas")
public class CitaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;

    @Column(name = "medico_id", nullable = false)
    private UUID medicoId;

    @Column(name = "franja_horaria_id", nullable = false)
    private UUID franjaHorariaId;

    @Column(name = "numero_referencia", nullable = false, unique = true)
    private String numeroReferencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCita estado;

    @Column(name = "creada_en", nullable = false)
    private LocalDateTime creadaEn;

    protected CitaEntity() {}

    public CitaEntity(UUID id, UUID pacienteId, UUID medicoId, UUID franjaHorariaId,
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
