package org.citasalud.infrastructure.persistence.repository;

import org.citasalud.infrastructure.persistence.entity.NotificacionWhatsAppEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificacionWhatsAppJpaRepository extends JpaRepository<NotificacionWhatsAppEntity, UUID> {
    List<NotificacionWhatsAppEntity> findByCitaId(UUID citaId);
}
