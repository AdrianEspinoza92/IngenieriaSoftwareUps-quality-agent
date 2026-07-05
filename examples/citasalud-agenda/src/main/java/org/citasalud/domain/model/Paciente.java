package org.citasalud.domain.model;

import java.util.UUID;

public class Paciente {

    private final UUID id;
    private final String nombre;
    private final String numeroWhatsApp;

    public Paciente(UUID id, String nombre, String numeroWhatsApp) {
        this.id = id;
        this.nombre = nombre;
        this.numeroWhatsApp = numeroWhatsApp;
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getNumeroWhatsApp() { return numeroWhatsApp; }
}
