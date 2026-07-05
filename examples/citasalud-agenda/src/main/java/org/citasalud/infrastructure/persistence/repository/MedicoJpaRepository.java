package org.citasalud.infrastructure.persistence.repository;

import org.citasalud.infrastructure.persistence.entity.MedicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MedicoJpaRepository extends JpaRepository<MedicoEntity, UUID> {
}
