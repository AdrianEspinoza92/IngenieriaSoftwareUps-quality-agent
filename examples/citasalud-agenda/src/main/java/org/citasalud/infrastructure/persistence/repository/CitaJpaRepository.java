package org.citasalud.infrastructure.persistence.repository;

import org.citasalud.infrastructure.persistence.entity.CitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CitaJpaRepository extends JpaRepository<CitaEntity, UUID> {
}
