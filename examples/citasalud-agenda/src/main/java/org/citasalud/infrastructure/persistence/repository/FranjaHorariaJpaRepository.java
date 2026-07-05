package org.citasalud.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import org.citasalud.infrastructure.persistence.entity.FranjaHorariaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FranjaHorariaJpaRepository extends JpaRepository<FranjaHorariaEntity, UUID> {
    List<FranjaHorariaEntity> findByMedicoIdAndFecha(UUID medicoId, LocalDate fecha);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT f FROM FranjaHorariaEntity f WHERE f.id = :id")
    Optional<FranjaHorariaEntity> findByIdForUpdate(@Param("id") UUID id);
}
