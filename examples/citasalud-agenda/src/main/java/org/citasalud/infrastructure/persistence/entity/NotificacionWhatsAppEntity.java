package org.citasalud.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.citasalud.domain.model.EstadoNotificacion;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificaciones_whatsapp")
public class NotificacionWhatsAppEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "cita_id", nullable = false)
    private UUID citaId;

    @Column(name = "destinatario")
    private String destinatario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoNotificacion estado;

    @Column(nullable = false)
    private int intentos;

    @Column(name = "ultimo_intento")
    private LocalDateTime ultimoIntento;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    protected NotificacionWhatsAppEntity() {}

    public NotificacionWhatsAppEntity(UUID id, UUID citaId, String destinatario,
                                      EstadoNotificacion estado, int intentos,
                                      LocalDateTime ultimoIntento, String mensaje) {
        this.id = id;
        this.citaId = citaId;
        this.destinatario = destinatario;
        this.estado = estado;
        this.intentos = intentos;
        this.ultimoIntento = ultimoIntento;
        this.mensaje = mensaje;
    }

    public UUID getId() { return id; }
    public UUID getCitaId() { return citaId; }
    public String getDestinatario() { return destinatario; }
    public EstadoNotificacion getEstado() { return estado; }
    public int getIntentos() { return intentos; }
    public LocalDateTime getUltimoIntento() { return ultimoIntento; }
    public String getMensaje() { return mensaje; }
}
