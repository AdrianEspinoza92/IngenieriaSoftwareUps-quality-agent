package org.citasalud.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "pacientes")
public class PacienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "numero_whatsapp", nullable = false)
    private String numeroWhatsApp;

    protected PacienteEntity() {}

    public PacienteEntity(UUID id, String nombre, String numeroWhatsApp) {
        this.id = id;
        this.nombre = nombre;
        this.numeroWhatsApp = numeroWhatsApp;
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getNumeroWhatsApp() { return numeroWhatsApp; }
}
