package org.citasalud.config;

import org.citasalud.application.usecase.ConsultarDisponibilidadUseCaseImpl;
import org.citasalud.application.usecase.ReservarCitaUseCaseImpl;
import org.citasalud.domain.port.in.ConsultarDisponibilidadUseCase;
import org.citasalud.domain.port.in.ReservarCitaUseCase;
import org.citasalud.domain.port.out.*;
import org.citasalud.infrastructure.notification.WhatsAppNotificationAdapter;
import org.citasalud.infrastructure.persistence.adapter.*;
import org.citasalud.infrastructure.persistence.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public MedicoRepositoryPort medicoRepositoryPort(MedicoJpaRepository jpaRepository) {
        return new MedicoRepositoryAdapter(jpaRepository);
    }

    @Bean
    public PacienteRepositoryPort pacienteRepositoryPort(PacienteJpaRepository jpaRepository) {
        return new PacienteRepositoryAdapter(jpaRepository);
    }

    @Bean
    public FranjaHorariaRepositoryPort franjaHorariaRepositoryPort(FranjaHorariaJpaRepository jpaRepository) {
        return new FranjaHorariaRepositoryAdapter(jpaRepository);
    }

    @Bean
    public CitaRepositoryPort citaRepositoryPort(CitaJpaRepository jpaRepository) {
        return new CitaRepositoryAdapter(jpaRepository);
    }

    @Bean
    public NotificacionWhatsAppPort notificacionWhatsAppPort(NotificacionWhatsAppJpaRepository jpaRepository) {
        return new WhatsAppNotificationAdapter(jpaRepository);
    }

    @Bean
    public ConsultarDisponibilidadUseCase consultarDisponibilidadUseCase(
            MedicoRepositoryPort medicoRepo, FranjaHorariaRepositoryPort franjaRepo) {
        return new ConsultarDisponibilidadUseCaseImpl(medicoRepo, franjaRepo);
    }

    @Bean
    public ReservarCitaUseCase reservarCitaUseCase(
            FranjaHorariaRepositoryPort franjaRepo,
            CitaRepositoryPort citaRepo,
            MedicoRepositoryPort medicoRepo,
            PacienteRepositoryPort pacienteRepo,
            NotificacionWhatsAppPort notificacionPort) {
        return new ReservarCitaUseCaseImpl(franjaRepo, citaRepo, medicoRepo, pacienteRepo, notificacionPort);
    }
}
