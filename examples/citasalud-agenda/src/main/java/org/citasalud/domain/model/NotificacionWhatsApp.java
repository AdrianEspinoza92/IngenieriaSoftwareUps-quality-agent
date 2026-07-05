package org.citasalud.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificacionWhatsApp {

    private final UUID id;
    private final UUID citaId;
    private EstadoNotificacion estado;
    private int intentos;
    private LocalDateTime ultimoIntento;

    public NotificacionWhatsApp(UUID id, UUID citaId, EstadoNotificacion estado,
                                int intentos, LocalDateTime ultimoIntento) {
        this.id = id;
        this.citaId = citaId;
        this.estado = estado;
        this.intentos = intentos;
        this.ultimoIntento = ultimoIntento;
    }

    public UUID getId() { return id; }
    public UUID getCitaId() { return citaId; }
    public EstadoNotificacion getEstado() { return estado; }
    public int getIntentos() { return intentos; }
    public LocalDateTime getUltimoIntento() { return ultimoIntento; }
}
