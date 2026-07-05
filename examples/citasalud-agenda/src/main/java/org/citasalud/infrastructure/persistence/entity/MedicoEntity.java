package org.citasalud.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "medicos")
public class MedicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String especialidad;

    protected MedicoEntity() {}

    public MedicoEntity(UUID id, String nombre, String especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEspecialidad() { return especialidad; }
}
