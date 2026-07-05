# Data Model: Reserva de Cita en Línea 24/7

**Feature**: US-01 / Épica E-01
**Date**: 2026-06-27

## Entidades de Dominio

Las entidades de dominio son objetos Java puros (sin anotaciones de framework) ubicadas en
`org.citasalud.domain.model`. Las entidades de persistencia JPA (con `@Entity`) están en
`org.citasalud.infrastructure.adapter.out.persistence.entity` y son mapeadas desde/hacia el dominio.

---

### Paciente

Persona registrada en el sistema que puede reservar citas médicas.

| Campo       | Tipo     | Restricciones                            |
|-------------|----------|------------------------------------------|
| id          | UUID     | Obligatorio, inmutable, único            |
| nombre      | String   | Obligatorio, 2–100 caracteres            |
| apellido    | String   | Obligatorio, 2–100 caracteres            |
| telefono    | String   | Obligatorio, número WhatsApp con prefijo |
| email       | String   | Opcional, formato email válido           |

**Invariants**: El teléfono debe incluir código de país (ej. +593912345678).

---

### Medico

Profesional de salud registrado con su especialidad y agenda de franjas horarias.

| Campo        | Tipo     | Restricciones                         |
|--------------|----------|---------------------------------------|
| id           | UUID     | Obligatorio, inmutable, único         |
| nombre       | String   | Obligatorio, 2–150 caracteres         |
| especialidad | String   | Obligatorio, no vacío                 |

---

### FranjaHoraria

Intervalo de tiempo en la agenda de un médico para una fecha específica. Es la unidad de
disponibilidad del sistema.

| Campo       | Tipo         | Restricciones                                    |
|-------------|--------------|--------------------------------------------------|
| id          | UUID         | Obligatorio, inmutable, único                    |
| medicoId    | UUID         | FK a Médico, obligatorio                         |
| fecha       | LocalDate    | Obligatorio, no puede ser fecha pasada            |
| horaInicio  | LocalTime    | Obligatorio, múltiplo de 30 min                  |
| horaFin     | LocalTime    | Obligatorio, horaFin = horaInicio + 30 min       |
| estado      | EstadoFranja | Obligatorio; valores: `DISPONIBLE`, `OCUPADA`    |
| version     | Long         | Control de concurrencia (optimistic locking)     |

**Invariants**:
- `horaFin` siempre es `horaInicio + 30 minutos`.
- Un cambio de `DISPONIBLE` → `OCUPADA` es el único estado que puede causar conflicto de concurrencia.
- El estado `OCUPADA` no puede volver a `DISPONIBLE` en este US (cancelación fuera del alcance).

**State Transitions**:
```
DISPONIBLE ──(reservar)──► OCUPADA
```

---

### Cita

Reserva confirmada entre un paciente y un médico en una franja horaria específica.

| Campo            | Tipo      | Restricciones                                      |
|------------------|-----------|----------------------------------------------------|
| id               | UUID      | Obligatorio, inmutable, único                      |
| pacienteId       | UUID      | FK a Paciente, obligatorio                         |
| franjaHorariaId  | UUID      | FK a FranjaHoraria (OCUPADA), obligatorio          |
| estado           | EstadoCita| Obligatorio; valores: `CONFIRMADA`, `CANCELADA`, `COMPLETADA` |
| numeroReferencia | String    | Obligatorio, único, formato: `CIT-YYYY-NNNNN`     |
| creadaEn         | Instant   | Obligatorio, timestamp de creación                 |

**State Transitions**:
```
CONFIRMADA ──(completar)──► COMPLETADA
CONFIRMADA ──(cancelar)───► CANCELADA   [fuera del alcance de este US]
```

---

### NotificacionWhatsApp

Registro de intentos de envío de confirmación al paciente por WhatsApp.

| Campo           | Tipo              | Restricciones                                |
|-----------------|-------------------|----------------------------------------------|
| id              | UUID              | Obligatorio, inmutable, único                |
| citaId          | UUID              | FK a Cita, obligatorio                       |
| destinatario    | String            | Número WhatsApp del paciente                 |
| estado          | EstadoNotificacion| `PENDIENTE`, `ENVIADA`, `FALLIDA`            |
| intentos        | Integer           | Default 0, incrementa con cada intento       |
| ultimoIntento   | Instant           | Null hasta el primer intento                 |
| mensaje         | String            | Contenido enviado al paciente                |

---

## Enumeraciones de Dominio

```java
// org.citasalud.domain.model.EstadoFranja
enum EstadoFranja { DISPONIBLE, OCUPADA }

// org.citasalud.domain.model.EstadoCita
enum EstadoCita { CONFIRMADA, CANCELADA, COMPLETADA }

// org.citasalud.domain.model.EstadoNotificacion
enum EstadoNotificacion { PENDIENTE, ENVIADA, FALLIDA }
```

---

## Puertos de Dominio (Interfaces)

### Puertos de Entrada (in) — Casos de Uso

```java
// org.citasalud.domain.port.in.ConsultarDisponibilidadUseCase
interface ConsultarDisponibilidadUseCase {
    List<Medico>        listarMedicos(String especialidad);
    List<FranjaHoraria> consultarDisponibilidad(UUID medicoId, LocalDate fecha);
}

// org.citasalud.domain.port.in.ReservarCitaUseCase
interface ReservarCitaUseCase {
    Cita reservar(UUID pacienteId, UUID medicoId, UUID franjaHorariaId, LocalDate fecha);
}
```

### Puertos de Salida (out) — Repositorios y Servicios Externos

```java
// org.citasalud.domain.port.out.MedicoRepositoryPort
interface MedicoRepositoryPort {
    List<Medico> findByEspecialidad(String especialidad);
    Optional<Medico> findById(UUID id);
}

// org.citasalud.domain.port.out.FranjaHorariaRepositoryPort
interface FranjaHorariaRepositoryPort {
    List<FranjaHoraria> findByMedicoAndFecha(UUID medicoId, LocalDate fecha);
    Optional<FranjaHoraria> findByIdForUpdate(UUID id); // con bloqueo optimista
    FranjaHoraria save(FranjaHoraria franja);
}

// org.citasalud.domain.port.out.CitaRepositoryPort
interface CitaRepositoryPort {
    Cita save(Cita cita);
    Optional<Cita> findById(UUID id);
}

// org.citasalud.domain.port.out.NotificacionWhatsAppPort
interface NotificacionWhatsAppPort {
    void enviarConfirmacion(Cita cita, Paciente paciente);
}
```

---

## Excepción de Dominio

```java
// org.citasalud.domain.exception.FranjaNoDisponibleException
class FranjaNoDisponibleException extends RuntimeException {
    private final UUID franjaId;
    // Constructor con mensaje y franjaId
}
```

---

## Relaciones entre Entidades

```
Medico 1 ──── * FranjaHoraria
Paciente 1 ──── * Cita
FranjaHoraria 1 ──── 0..1 Cita
Cita 1 ──── 1 NotificacionWhatsApp
```

---

## Reglas de Generación de Número de Referencia

Formato: `CIT-{YYYY}-{N5}` donde `{YYYY}` es el año de creación y `{N5}` es un número secuencial
de 5 dígitos con cero-padding (ej. `CIT-2026-00001`). Generado en la capa de aplicación al crear
la `Cita`.
