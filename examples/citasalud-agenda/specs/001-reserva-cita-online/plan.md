# Implementation Plan: Reserva de Cita en Línea 24/7

**Branch**: `001-reserva-cita-online` | **Date**: 2026-06-27 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `specs/001-reserva-cita-online/spec.md`

## Summary

Implementar el módulo de reserva de citas médicas en línea (US-01, Épica E-01, 8 pts) que permita
a los pacientes seleccionar médico, fecha y franja horaria disponible y confirmar la cita en
cualquier momento del día, recibiendo confirmación por WhatsApp. El sistema debe manejar
concurrencia para evitar doble reserva de la misma franja. La arquitectura sigue Clean Architecture
(Robert C. Martin); las APIs se definen mediante contrato OpenAPI 3.0 y se generan con
openapi-generator-gradle-plugin.

## Technical Context

**Language/Version**: Java 17 (LTS)

**Primary Dependencies**: Spring Boot 3.x (Web, Data JPA, Validation), openapi-generator-gradle-plugin
5.x, Cucumber 7.x (JUnit Platform Engine), Mockito 5.x, H2 (tests), PostgreSQL (producción),
JaCoCo Gradle Plugin

**Storage**: PostgreSQL (producción) / H2 embebida (pruebas de integración)

**Testing**: JUnit 5 (jupiter) + Mockito (unitarias) + Cucumber (funcionales/BDD) + Spring Boot Test
(integración)

**Target Platform**: Servidor Linux — servicio REST desplegable como JAR ejecutable

**Project Type**: web-service (REST API backend)

**Performance Goals**: Confirmación de reserva en < 2 s p95; notificación WhatsApp en < 60 s;
disponibilidad del servicio ≥ 99.5 % mensual

**Constraints**: Control de concurrencia obligatorio (sin doble reserva); clases generadas por
openapi-generator excluidas de métricas JaCoCo; cobertura por clase > 80 %, global ≥ 80 %

**Scale/Scope**: Fase inicial — decenas de médicos, cientos de pacientes concurrentes; escalabilidad
vertical suficiente para esta épica

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **I. Arquitectura Limpia**: Capas identificadas — `domain` (Entidades + Puertos), `application`
  (Casos de Uso), `infrastructure` (Adaptadores JPA + WhatsApp + REST). Dependencias solo apuntan
  hacia adentro; el dominio no importa Spring ni JPA.
- [x] **II. BDD Testing**: Escenarios Given-When-Then definidos en `spec.md` (US1, US2, US3).
  Tres capas planificadas: unitarias (dominio/casos de uso), integración (repositorios + DB), funcionales
  (Cucumber feature files cubriendo los criterios de aceptación).
- [x] **III. SOLID/YAGNI/DRY**: Cada Use Case tiene única responsabilidad. Puertos e implementaciones
  separados (DIP). Sin funcionalidad especulativa (cancelación/modificación fuera de este US). Entidades
  de dominio reutilizadas entre casos de uso.
- [x] **IV. API First**: Contrato `specs/001-reserva-cita-online/contracts/openapi.yml` generado en
  Phase 1 antes de cualquier implementación. Tarea de generación con `openapi-generator-gradle-plugin`
  incluida en `tasks.md`.
- [x] **V. JaCoCo**: Plugin `jacoco` añadido en `build.gradle`. Umbrales: clase > 80 %, global ≥ 80 %.
  Tarea `jacocoTestCoverageVerification` incluida; clases generadas excluidas. Build falla si no se
  cumplen los umbrales.

## Project Structure

### Documentation (this feature)

```text
specs/001-reserva-cita-online/
├── plan.md              # Este archivo
├── research.md          # Phase 0 — decisiones tecnológicas
├── data-model.md        # Phase 1 — modelo de dominio y entidades
├── quickstart.md        # Phase 1 — guía de validación end-to-end
├── contracts/
│   └── openapi.yml      # Phase 1 — contrato API First (OpenAPI 3.0)
└── tasks.md             # Phase 2 — generado por /speckit-tasks
```

### Source Code (repository root)

```text
src/
├── main/
│   ├── java/
│   │   └── org/citasalud/
│   │       ├── domain/
│   │       │   ├── model/
│   │       │   │   ├── Cita.java
│   │       │   │   ├── Paciente.java
│   │       │   │   ├── Medico.java
│   │       │   │   ├── FranjaHoraria.java
│   │       │   │   ├── EstadoCita.java
│   │       │   │   └── EstadoFranja.java
│   │       │   ├── port/
│   │       │   │   ├── in/
│   │       │   │   │   ├── ReservarCitaUseCase.java
│   │       │   │   │   └── ConsultarDisponibilidadUseCase.java
│   │       │   │   └── out/
│   │       │   │       ├── CitaRepositoryPort.java
│   │       │   │       ├── FranjaHorariaRepositoryPort.java
│   │       │   │       ├── MedicoRepositoryPort.java
│   │       │   │       └── NotificacionWhatsAppPort.java
│   │       │   └── exception/
│   │       │       └── FranjaNoDisponibleException.java
│   │       ├── application/
│   │       │   └── usecase/
│   │       │       ├── ReservarCitaUseCaseImpl.java
│   │       │       └── ConsultarDisponibilidadUseCaseImpl.java
│   │       └── infrastructure/
│   │           ├── adapter/
│   │           │   ├── in/
│   │           │   │   └── rest/
│   │           │   │       └── CitasController.java
│   │           │   └── out/
│   │           │       ├── persistence/
│   │           │       │   ├── CitaJpaRepository.java
│   │           │       │   ├── FranjaHorariaJpaRepository.java
│   │           │       │   ├── MedicoJpaRepository.java
│   │           │       │   └── entity/
│   │           │       │       ├── CitaEntity.java
│   │           │       │       ├── FranjaHorariaEntity.java
│   │           │       │       └── MedicoEntity.java
│   │           │       └── whatsapp/
│   │           │           └── WhatsAppNotificationAdapter.java
│   │           └── config/
│   │               └── BeanConfiguration.java
│   └── resources/
│       ├── application.yml
│       ├── application-prod.yml
│       └── db/
│           ├── schema.sql           # DDL: tablas medicos, franjas_horarias, citas, notificaciones_whatsapp
│           └── data.sql             # DML: médicos y franjas horarias pre-cargadas (datos semilla prod)
├── test/
│   ├── java/
│   │   └── org/citasalud/
│   │       ├── domain/
│   │       │   └── usecase/        # Pruebas unitarias (JUnit 5 + Mockito)
│   │       ├── infrastructure/
│   │       │   └── adapter/        # Pruebas de integración (Spring Boot Test)
│   │       └── cucumber/
│   │           └── steps/          # Step definitions BDD
│   └── resources/
│       ├── application-test.yml
│       ├── features/
│       │   ├── reserva_cita.feature
│       │   └── disponibilidad.feature
│       └── db/
│           └── data-test.sql        # DML: datos de prueba para H2 (médicos, franjas DISPONIBLE y OCUPADA)
build/
└── generated/                      # Código generado por openapi-generator (no editar)
    └── src/main/java/
        └── org/citasalud/api/
            ├── CitasApi.java        # Interfaz generada
            └── model/               # DTOs generados
```

**Structure Decision**: Arquitectura Hexagonal (Ports & Adapters) dentro de Clean Architecture.
Un solo proyecto Java/Gradle. El dominio en `org.citasalud.domain` es independiente de Spring.
Los adaptadores REST implementan la interfaz generada por openapi-generator.

## Complexity Tracking

> Sin violaciones de constitución que justificar.
