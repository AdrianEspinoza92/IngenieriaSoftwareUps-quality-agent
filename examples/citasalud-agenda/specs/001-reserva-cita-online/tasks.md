---
description: "Task list — Reserva de Cita en Línea 24/7 (US-01 / Épica E-01)"
---

# Tasks: Reserva de Cita en Línea 24/7

**Input**: Documentos de diseño en `specs/001-reserva-cita-online/`

**Prerequisites**: plan.md ✅ spec.md ✅ research.md ✅ data-model.md ✅ contracts/openapi.yml ✅

**Tests**: BDD Testing es **OBLIGATORIO** (Principio II NON-NEGOTIABLE de la Constitución).
Tres capas requeridas: unitarias (JUnit 5 + Mockito), integración (Spring Boot Test + H2) y
funcionales/aceptación (Cucumber). Ciclo Red → Green → Refactor en cada User Story.

**Constitución activa**: `.specify/memory/constitution.md` v1.0.0

## Formato: `[ID] [P?] [Story?] Descripción con ruta exacta`

- **[P]**: Puede ejecutarse en paralelo (archivos distintos, sin dependencias pendientes)
- **[Story]**: User Story a la que pertenece (US1, US2, US3)
- Rutas relativas al root del repositorio

## Path Conventions

- Código fuente: `src/main/java/org/citasalud/`
- Tests: `src/test/java/org/citasalud/`
- Feature files BDD: `src/test/resources/features/`
- DB schemas/seeds main: `src/main/resources/db/`
- DB seeds test: `src/test/resources/db/`
- Código generado (NO editar): `build/generated/src/main/java/org/citasalud/api/`

---

## Phase 1: Setup (Infraestructura del Proyecto)

**Propósito**: Configurar Gradle con dependencias, plugins y archivos de configuración base.

- [X] T001 Agregar al `build.gradle` las dependencias: `spring-boot-starter-web`,
  `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `h2` (testRuntimeOnly),
  `postgresql` (runtimeOnly), `cucumber-java`, `cucumber-junit-platform-engine`,
  `mockito-core`; y aplicar plugins `org.springframework.boot` y `io.spring.dependency-management`
- [X] T002 [P] Configurar la tarea `openApiGenerate` en `build.gradle`: `generatorName = "spring"`,
  `inputSpec = "$rootDir/specs/001-reserva-cita-online/contracts/openapi.yml"`,
  `outputDir = "$buildDir/generated"`, `apiPackage = "org.citasalud.api"`,
  `modelPackage = "org.citasalud.api.model"`, `configOptions = [interfaceOnly:"true", useSpringBoot3:"true"]`;
  agregar `build/generated/src/main/java` al sourceSets main
- [X] T003 [P] Configurar plugin `jacoco` en `build.gradle`: tarea `jacocoTestReport` ejecuta
  después de `test`; tarea `jacocoTestCoverageVerification` con regla global `INSTRUCTION >= 0.80`
  y regla por `CLASS` `INSTRUCTION >= 0.80`; excluir `org/citasalud/api/**` y
  `org/citasalud/api/model/**` de las métricas
- [X] T004 [P] Crear `src/main/resources/application.yml` con: `server.port: 8080`,
  `spring.datasource` (PostgreSQL, variables de entorno `${DB_URL}`, `${DB_USER}`, `${DB_PASS}`),
  `spring.jpa.hibernate.ddl-auto: validate`,
  `spring.sql.init.schema-locations: classpath:db/schema.sql`,
  `spring.sql.init.data-locations: classpath:db/data.sql`,
  `spring.sql.init.mode: always`
- [X] T005 [P] Crear `src/test/resources/application-test.yml` con datasource H2 en memoria
  (`jdbc:h2:mem:citasaludtest`), `spring.jpa.hibernate.ddl-auto: create-drop`,
  `spring.sql.init.schema-locations: classpath:db/schema.sql`,
  `spring.sql.init.data-locations: classpath:db/data-test.sql`,
  `spring.sql.init.mode: always`
- [X] T006 Ejecutar `./gradlew openApiGenerate` y verificar que existen
  `build/generated/src/main/java/org/citasalud/api/CitasApi.java`,
  `MedicosApi.java` y los DTOs en `org/citasalud/api/model/`

---

## Phase 2: Foundational (Dominio, Persistencia y DB Seed)

**Propósito**: Dominio puro, puertos, entidades JPA y scripts SQL cargados en resources.
**⚠️ CRÍTICO**: Ninguna User Story puede comenzar hasta que esta fase esté completa.

- [X] T007 [P] Crear enumeraciones de dominio en `src/main/java/org/citasalud/domain/model/`:
  `EstadoCita.java` (CONFIRMADA, CANCELADA, COMPLETADA),
  `EstadoFranja.java` (DISPONIBLE, OCUPADA),
  `EstadoNotificacion.java` (PENDIENTE, ENVIADA, FALLIDA)
- [X] T008 [P] Crear `FranjaNoDisponibleException` en
  `src/main/java/org/citasalud/domain/exception/FranjaNoDisponibleException.java`
  con campo `UUID franjaId` y constructor `(String message, UUID franjaId)`
- [X] T009 [P] Crear entidades de dominio `Paciente.java` y `Medico.java` en
  `src/main/java/org/citasalud/domain/model/` (objetos Java puros, sin anotaciones de framework)
- [X] T010 Crear entidad de dominio `FranjaHoraria.java` en
  `src/main/java/org/citasalud/domain/model/FranjaHoraria.java`
  con campos: `UUID id`, `UUID medicoId`, `LocalDate fecha`, `LocalTime horaInicio`,
  `LocalTime horaFin`, `EstadoFranja estado`, `Long version`; invariante: `horaFin = horaInicio + 30 min`
- [X] T011 Crear entidad de dominio `Cita.java` en
  `src/main/java/org/citasalud/domain/model/Cita.java`
  con campos: `UUID id`, `UUID pacienteId`, `UUID franjaHorariaId`, `EstadoCita estado`,
  `String numeroReferencia`, `Instant creadaEn`; método factoría estático `Cita.crear(...)`
- [X] T012 [P] Crear entidad de dominio `NotificacionWhatsApp.java` en
  `src/main/java/org/citasalud/domain/model/NotificacionWhatsApp.java`
  con campos: `UUID id`, `UUID citaId`, `String destinatario`, `EstadoNotificacion estado`,
  `int intentos`, `Instant ultimoIntento`, `String mensaje`
- [X] T013 [P] Crear interfaces de puertos de salida en `src/main/java/org/citasalud/domain/port/out/`:
  `MedicoRepositoryPort.java` (`findByEspecialidad`, `findById`),
  `FranjaHorariaRepositoryPort.java` (`findByMedicoAndFecha`, `findByIdForUpdate`, `save`),
  `CitaRepositoryPort.java` (`save`, `findById`),
  `PacienteRepositoryPort.java` (`findById(UUID id): Optional<Paciente>` — lectura del paciente
  registrado para obtener su teléfono WhatsApp al construir la notificación),
  `NotificacionWhatsAppPort.java` (`enviarConfirmacion(Cita cita, Medico medico, FranjaHoraria franja, Paciente paciente)`
  — firma completa para que el adaptador construya el mensaje con todos los campos de FR-008)
- [X] T014 [P] Crear interfaces de puertos de entrada en `src/main/java/org/citasalud/domain/port/in/`:
  `ConsultarDisponibilidadUseCase.java` (`listarMedicos(String especialidad)`,
  `consultarDisponibilidad(UUID medicoId, LocalDate fecha)`) y
  `ReservarCitaUseCase.java` (`reservar(UUID pacienteId, UUID medicoId, UUID franjaHorariaId, LocalDate fecha)`)
- [X] T015 [P] Crear entidades JPA en
  `src/main/java/org/citasalud/infrastructure/adapter/out/persistence/entity/`:
  `MedicoEntity.java` (`@Entity @Table("medicos")`),
  `FranjaHorariaEntity.java` (`@Entity @Table("franjas_horarias")` con `@Version Long version`),
  `CitaEntity.java` (`@Entity @Table("citas")`),
  `NotificacionWhatsAppEntity.java` (`@Entity @Table("notificaciones_whatsapp")` con campos
  `citaId UUID`, `destinatario String`, `estado String`, `intentos int`, `ultimoIntento Instant`,
  `mensaje String`)
- [X] T016 [P] Crear repositorios Spring Data JPA en
  `src/main/java/org/citasalud/infrastructure/adapter/out/persistence/`:
  `MedicoJpaRepository.java` (extiende `JpaRepository<MedicoEntity, UUID>`),
  `FranjaHorariaJpaRepository.java` (con `@Query` para `findByMedicoIdAndFecha`),
  `CitaJpaRepository.java`,
  `NotificacionWhatsAppJpaRepository.java` (extiende `JpaRepository<NotificacionWhatsAppEntity, UUID>`
  con método `findByCitaId(UUID citaId)`)
- [X] T017 Crear `src/main/resources/db/schema.sql` con DDL completo:
  ```sql
  CREATE TABLE medicos (id UUID PRIMARY KEY, nombre VARCHAR(150), especialidad VARCHAR(100));
  CREATE TABLE franjas_horarias (id UUID PRIMARY KEY, medico_id UUID REFERENCES medicos(id),
    fecha DATE, hora_inicio TIME, hora_fin TIME, estado VARCHAR(20), version BIGINT DEFAULT 0);
  CREATE TABLE citas (id UUID PRIMARY KEY, paciente_id UUID, franja_horaria_id UUID
    REFERENCES franjas_horarias(id), estado VARCHAR(20), numero_referencia VARCHAR(20) UNIQUE,
    creada_en TIMESTAMP);
  CREATE TABLE notificaciones_whatsapp (id UUID PRIMARY KEY, cita_id UUID REFERENCES citas(id),
    destinatario VARCHAR(20), estado VARCHAR(20), intentos INT DEFAULT 0,
    ultimo_intento TIMESTAMP, mensaje TEXT);
  ```
- [X] T018 Crear `src/main/resources/db/data.sql` con datos semilla de producción:
  INSERT de mínimo 2 médicos (Medicina General, Pediatría) y 8 franjas DISPONIBLE distribuidas
  en 2 días futuros (4 franjas por médico por día, de 09:00 a 11:00 en intervalos de 30 min)
- [X] T019 Crear `src/test/resources/db/data-test.sql` con datos de prueba para H2:
  INSERT de 1 médico de prueba, 2 franjas DISPONIBLE (franjaDisponible1, franjaDisponible2)
  y 1 franja OCUPADA (franjaOcupada1) con UUIDs fijos para referenciar desde los tests
  (ej. `'00000000-0000-0000-0000-000000000001'`)

**Checkpoint**: Dominio, persistencia y DB seed listos — User Stories pueden comenzar.

---

## Phase 3: User Story 1 — Reserva Exitosa (Priority: P1) 🎯 MVP

**Goal**: Un paciente confirma una cita en franja disponible y recibe confirmación por WhatsApp.

**Independent Test**: `./gradlew test --tests "*ReservarCita*"` en verde;
POST `/api/v1/citas` retorna HTTP 201 con `estado: CONFIRMADA` y `numeroReferencia`.

### Tests para User Story 1 (REQUERIDO — BDD NON-NEGOTIABLE per Constitución II) ⚠️

> **IMPORTANTE: Escribir estos tests PRIMERO — DEBEN FALLAR antes de implementar**

- [X] T020 [P] [US1] Crear `src/test/resources/features/reserva_cita.feature` con escenarios US1
  en Given-When-Then: "Dado que hay franjas disponibles / Cuando el paciente elige médico fecha
  y franja y confirma / Entonces la cita queda con estado CONFIRMADA y se envía WhatsApp"
  (incluir 2 variantes: horario laboral y fuera de horario telefónico)
- [X] T021 [P] [US1] Crear `src/test/java/org/citasalud/domain/usecase/ReservarCitaUseCaseTest.java`
  con Mockito: mock de todos los puertos de salida; verificar que `CitaRepositoryPort.save` es
  invocado con estado CONFIRMADA y que `NotificacionWhatsAppPort.enviarConfirmacion` es invocado
  exactamente una vez
- [X] T022 [P] [US1] Crear `src/test/java/org/citasalud/infrastructure/adapter/in/rest/ReservarCitaIT.java`
  con `@SpringBootTest(webEnvironment=RANDOM_PORT)`, `@ActiveProfiles("test")`, usando datos de
  `data-test.sql`: POST `/api/v1/citas` con `franjaDisponible1` retorna HTTP 201 y
  `CitaResponse.estado = CONFIRMADA`
- [X] T023 [P] [US1] Crear step definitions `src/test/java/org/citasalud/cucumber/steps/ReservarCitaSteps.java`
  implementando los pasos Dado/Cuando/Entonces del feature file US1 usando `TestRestTemplate`

### Implementación para User Story 1

- [X] T024 [US1] Implementar `ConsultarDisponibilidadUseCaseImpl` en
  `src/main/java/org/citasalud/application/usecase/ConsultarDisponibilidadUseCaseImpl.java`:
  `listarMedicos` delega a `MedicoRepositoryPort.findByEspecialidad`;
  `consultarDisponibilidad` delega a `FranjaHorariaRepositoryPort.findByMedicoAndFecha`
- [X] T025 [US1] Implementar `ReservarCitaUseCaseImpl` en
  `src/main/java/org/citasalud/application/usecase/ReservarCitaUseCaseImpl.java`:
  obtener franja por id, verificar estado DISPONIBLE, marcar OCUPADA, generar
  `numeroReferencia` (`CIT-YYYY-NNNNN`), crear `Cita` vía `Cita.crear(...)`, persistir;
  obtener `Medico` vía `MedicoRepositoryPort.findById(franja.getMedicoId())` y `Paciente`
  vía `PacienteRepositoryPort.findById(pacienteId)`; invocar
  `NotificacionWhatsAppPort.enviarConfirmacion(cita, medico, franja, paciente)` (depende de T024)
- [X] T026 [P] [US1] Implementar `WhatsAppNotificationAdapter` en
  `src/main/java/org/citasalud/infrastructure/adapter/out/whatsapp/WhatsAppNotificationAdapter.java`
  implementando `NotificacionWhatsAppPort`: recibir `(Cita cita, Medico medico, FranjaHoraria franja,
  Paciente paciente)`, construir mensaje con nombre médico, especialidad, fecha, hora y
  `numeroReferencia` (FR-008), loguear y persistir `NotificacionWhatsAppEntity` vía
  `NotificacionWhatsAppJpaRepository` con estado ENVIADA (stub sincrónico);
  ⚠️ **Deuda técnica documentada**: reintento asíncrono ante fallo de WhatsApp queda fuera del
  alcance de esta épica — los campos `intentos` y `ultimoIntento` están modelados para una
  épica de reintentos futura
- [X] T027 [US1] Implementar `CitasController` en
  `src/main/java/org/citasalud/infrastructure/adapter/in/rest/CitasController.java`
  implementando la interfaz generada `CitasApi`: `reservarCita` (POST /citas → llama
  `ReservarCitaUseCase.reservar` → retorna `CitaResponse` HTTP 201) y `consultarCita`
  (GET /citas/{id} → retorna `CitaResponse` 200 o 404)
- [X] T028 [US1] Crear `BeanConfiguration` en
  `src/main/java/org/citasalud/infrastructure/config/BeanConfiguration.java`
  con `@Bean` para `ReservarCitaUseCaseImpl` y `ConsultarDisponibilidadUseCaseImpl`
  inyectando sus implementaciones de repositorio y `WhatsAppNotificationAdapter`
- [X] T029 [US1] Verificar que los tests de US1 pasan: `./gradlew test --tests "*ReservarCita*"`
  y confirmar HTTP 201 con `estado=CONFIRMADA` en el test de integración

**Checkpoint**: MVP funcional — POST /citas con franja disponible → 201 + WhatsApp.

---

## Phase 4: User Story 2 — Franja Ocupada (Priority: P2)

**Goal**: Cuando la franja elegida está ocupada, el sistema retorna 409 con alternativas.

**Independent Test**: `./gradlew test --tests "*FranjaOcupada*"` en verde;
POST `/api/v1/citas` con franja OCUPADA retorna HTTP 409 con `franjasAlternativas` no vacío.

### Tests para User Story 2 (REQUERIDO — BDD NON-NEGOTIABLE per Constitución II) ⚠️

> **IMPORTANTE: Escribir estos tests PRIMERO — DEBEN FALLAR antes de implementar**

- [X] T030 [P] [US2] Agregar escenarios US2 a `src/test/resources/features/reserva_cita.feature`:
  "Dado franja OCUPADA / Cuando el paciente intenta confirmarla / Entonces HTTP 409 con
  `codigo=FRANJA_OCUPADA` y `franjasAlternativas` no vacío"; y variante sin alternativas disponibles
- [X] T031 [P] [US2] Crear `src/test/java/org/citasalud/domain/usecase/FranjaOcupadaUseCaseTest.java`
  con Mockito: cuando `FranjaHoraria.estado == OCUPADA`, `ReservarCitaUseCase.reservar` lanza
  `FranjaNoDisponibleException`; verificar que `CitaRepositoryPort.save` NO es invocado
- [X] T032 [P] [US2] Crear `src/test/java/org/citasalud/infrastructure/adapter/in/rest/FranjaOcupadaIT.java`
  con `@SpringBootTest`, `@ActiveProfiles("test")`, usando `franjaOcupada1` de `data-test.sql`:
  POST `/api/v1/citas` retorna HTTP 409 con `ErrorResponse.codigo=FRANJA_OCUPADA`
  y `franjasAlternativas` con al menos un elemento
- [X] T033 [P] [US2] Crear step definitions `src/test/java/org/citasalud/cucumber/steps/FranjaOcupadaSteps.java`
  para los pasos US2 del feature file

### Implementación para User Story 2

- [X] T034 [US2] Actualizar `ReservarCitaUseCaseImpl` en
  `src/main/java/org/citasalud/application/usecase/ReservarCitaUseCaseImpl.java`:
  cuando estado == OCUPADA, consultar `FranjaHorariaRepositoryPort.findByMedicoAndFecha`
  para obtener alternativas y lanzar `FranjaNoDisponibleException` con la lista
- [X] T035 [US2] Crear `GlobalExceptionHandler` en
  `src/main/java/org/citasalud/infrastructure/adapter/in/rest/GlobalExceptionHandler.java`
  con `@RestControllerAdvice`: `FranjaNoDisponibleException` → HTTP 409 `ErrorResponse`
  con `codigo=FRANJA_OCUPADA` y `franjasAlternativas`; `OptimisticLockingFailureException` →
  relanzar como `FranjaNoDisponibleException`; excepciones genéricas → HTTP 500
- [X] T036 [US2] Verificar que los tests de US2 pasan: `./gradlew test --tests "*FranjaOcupada*"`
  y confirmar HTTP 409 con `franjasAlternativas` en el test de integración

**Checkpoint**: US1 + US2 completos — reserva exitosa y conflicto correctamente manejados.

---

## Phase 5: User Story 3 — Consulta de Disponibilidad (Priority: P3)

**Goal**: El paciente puede listar médicos y ver sus franjas horarias (DISPONIBLE / OCUPADA).

**Independent Test**: `./gradlew test --tests "*Medicos*"` en verde;
GET `/api/v1/medicos` retorna lista; GET `/api/v1/medicos/{id}/disponibilidad` retorna franjas.

### Tests para User Story 3 (REQUERIDO — BDD NON-NEGOTIABLE per Constitución II) ⚠️

> **IMPORTANTE: Escribir estos tests PRIMERO — DEBEN FALLAR antes de implementar**

- [X] T037 [P] [US3] Crear `src/test/resources/features/disponibilidad.feature` con escenarios US3:
  "Dado que el paciente accede al módulo / Cuando selecciona médico y fecha / Entonces ve franjas
  DISPONIBLE y OCUPADA diferenciadas"; y variante: "sin franjas → lista vacía"
- [X] T038 [P] [US3] Crear `src/test/java/org/citasalud/domain/usecase/ConsultarDisponibilidadUseCaseTest.java`
  con Mockito: verificar que `listarMedicos` delega a `MedicoRepositoryPort` y que
  `consultarDisponibilidad` retorna franjas en ambos estados (DISPONIBLE y OCUPADA)
- [X] T039 [P] [US3] Crear `src/test/java/org/citasalud/infrastructure/adapter/in/rest/MedicosControllerIT.java`
  con `@SpringBootTest`, `@ActiveProfiles("test")`:
  GET `/api/v1/medicos` → HTTP 200 con array de médicos;
  GET `/api/v1/medicos/{id}/disponibilidad?fecha=<fecha>` → HTTP 200 con `DisponibilidadResponse`
  que incluye franjas DISPONIBLE y OCUPADA del `data-test.sql`
- [X] T040 [P] [US3] Crear step definitions `src/test/java/org/citasalud/cucumber/steps/ConsultarDisponibilidadSteps.java`
  para los pasos US3 de `disponibilidad.feature`

### Implementación para User Story 3

- [X] T041 [US3] Crear `MedicosController` en
  `src/main/java/org/citasalud/infrastructure/adapter/in/rest/MedicosController.java`
  implementando la interfaz generada `MedicosApi`: `listarMedicos` (GET /medicos, parámetro
  opcional `especialidad`, retorna lista de `MedicoResponse`) y `consultarDisponibilidad`
  (GET /medicos/{medicoId}/disponibilidad?fecha, retorna `DisponibilidadResponse`)
- [X] T042 [US3] Actualizar `BeanConfiguration` en
  `src/main/java/org/citasalud/infrastructure/config/BeanConfiguration.java`
  para registrar `MedicosController` con su dependencia en `ConsultarDisponibilidadUseCase`
- [X] T043 [US3] Verificar que los tests de US3 pasan: `./gradlew test --tests "*Medicos*"`
  y Cucumber scenarios US3 en verde

**Checkpoint**: Las tres User Stories son independientemente funcionales y testeables.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Propósito**: Runner Cucumber unificado, validación JaCoCo y quickstart completo.

- [X] T044 [P] Crear `CucumberRunner` en
  `src/test/java/org/citasalud/cucumber/CucumberRunner.java` con
  `@Suite`, `@IncludeEngines("cucumber")` y
  `@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.citasalud.cucumber.steps")`
  y `@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "src/test/resources/features")`
- [X] T045 [P] Crear mappers `MedicoMapper`, `FranjaHorariaMapper` y `CitaMapper` en
  `src/main/java/org/citasalud/infrastructure/adapter/in/rest/mapper/` para convertir
  entidades de dominio a DTOs generados (`org.citasalud.api.model.*`) y viceversa
- [X] T046 Ejecutar la suite completa: `./gradlew test`
  — verificar que todos los tests (unitarios + integración + Cucumber) pasan en verde
- [X] T047 Generar y verificar métricas JaCoCo: `./gradlew jacocoTestReport jacocoTestCoverageVerification`
  — confirmar cobertura por clase > 80 % y global ≥ 80 % en
  `build/reports/jacoco/test/html/index.html`
- [X] T048 [P] Agregar `springdoc-openapi-starter-webmvc-ui` en `build.gradle` y verificar
  Swagger UI en `http://localhost:8080/swagger-ui.html` tras `./gradlew bootRun`
- [X] T049 Ejecutar validación del quickstart: seguir los 8 escenarios de
  `specs/001-reserva-cita-online/quickstart.md` contra el servidor local y confirmar
  todos los resultados esperados

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sin dependencias — iniciar de inmediato
- **Foundational (Phase 2)**: Requiere Phase 1 — **BLOQUEA todas las User Stories**
- **US1 (Phase 3)**: Requiere Phase 2 — primer MVP entregable
- **US2 (Phase 4)**: Requiere Phase 2 + T025 (ReservarCitaUseCaseImpl definido)
- **US3 (Phase 5)**: Requiere Phase 2; T024 ya implementado en US1
- **Polish (Phase 6)**: Requiere US1 + US2 + US3 completos

### Within Each User Story

1. Feature files + tests (todos deben estar en **ROJO** primero)
2. Use Case implementation
3. Controller/Adapter implementation
4. Verificación: todos los tests en **VERDE**

### Parallel Opportunities

```bash
# Phase 2 — lanzar en paralelo:
T007, T008, T009, T012, T013, T014, T015, T016

# Phase 3 (US1) — tests en paralelo (todos deben FALLAR primero):
T020, T021, T022, T023

# Phase 4 (US2) — tests en paralelo:
T030, T031, T032, T033

# Phase 5 (US3) — tests en paralelo:
T037, T038, T039, T040
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Phase 1: Setup
2. Phase 2: Foundational (**crítico — bloquea todo**)
3. Phase 3: US1 → reserva exitosa
4. **PARAR Y VALIDAR**: `./gradlew test` + quickstart escenarios 1–5
5. Demo: POST /citas → 201 + WhatsApp

### Incremental Delivery

1. Setup + Foundational → base lista
2. US1 → demo MVP
3. US2 → conflictos manejados
4. US3 → consulta de disponibilidad
5. Polish → JaCoCo ≥ 80 % + Swagger UI + quickstart validado

---

## Notes

- `[P]` = archivos distintos sin dependencias pendientes — lanzar en paralelo
- Tests BDD DEBEN fallar antes de implementar (Principio II NON-NEGOTIABLE)
- `src/main/resources/db/schema.sql` y `data.sql` se cargan automáticamente con Spring Boot
- `src/test/resources/db/data-test.sql` usa UUIDs fijos para referencias predecibles en tests
- Código generado en `build/generated/` nunca se edita manualmente (Principio IV)
- `./gradlew jacocoTestCoverageVerification` debe pasar antes de todo PR (Principio V)
