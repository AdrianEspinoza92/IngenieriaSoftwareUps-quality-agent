# Feature Specification: Reserva de Cita en Línea 24/7

**Feature Branch**: `001-reserva-cita-online`

**Created**: 2026-06-27

**Status**: Draft

**Épica**: E-01 | **Historia**: US-01 | **Puntos**: 8

> **Constitución**: Los escenarios de aceptación siguen formato Given-When-Then (BDD — Principio II).
> El contrato OpenAPI DEBE existir en `contracts/openapi.yml` antes de la implementación (API First — Principio IV).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Reserva Exitosa de Cita Disponible (Priority: P1)

Como paciente, quiero seleccionar un médico, fecha y franja horaria disponible y confirmar la cita,
para recibir una confirmación inmediata por WhatsApp y asegurar mi atención sin necesidad de llamar
por teléfono, en cualquier momento del día.

**Why this priority**: Es el flujo principal del negocio. Sin este flujo funcionando no hay valor
alguno para el paciente ni para la clínica. Representa la razón de existir de la funcionalidad.

**Independent Test**: El flujo es completamente testeable reservando una cita con un médico y franja
disponibles; el resultado es una cita registrada en el sistema y un mensaje de WhatsApp enviado al
paciente.

**Acceptance Scenarios**:

1. **Given** el paciente accede al sistema fuera del horario de atención telefónica y hay franjas disponibles,
   **When** elige un médico, selecciona una fecha y una franja horaria disponible y confirma la reserva,
   **Then** la cita queda registrada en el sistema con estado "confirmada", la franja pasa a estado "ocupada"
   y el paciente recibe una notificación de confirmación por WhatsApp con los detalles de la cita.

2. **Given** el paciente accede al sistema en cualquier horario (incluyendo fines de semana y festivos),
   **When** completa el formulario de reserva con datos válidos y confirma,
   **Then** el sistema procesa la reserva exitosamente sin requerir intervención humana.

---

### User Story 2 - Intento de Reserva en Franja Ocupada (Priority: P2)

Como paciente, quiero que el sistema me informe cuando una franja horaria ya está ocupada y me ofrezca
alternativas, para no perder tiempo intentando reservar un horario no disponible.

**Why this priority**: Es el flujo de error más frecuente en sistemas de reserva concurrentes. Un
mal manejo genera frustración y abandono del proceso por parte del paciente.

**Independent Test**: Testeable intentando reservar una franja ya ocupada; el sistema debe rechazar
la acción y presentar opciones alternativas al paciente sin registrar duplicidad.

**Acceptance Scenarios**:

1. **Given** la franja horaria seleccionada ya fue reservada por otro paciente,
   **When** el paciente intenta confirmar esa franja,
   **Then** el sistema muestra un mensaje claro indicando que la franja no está disponible
   y presenta otras franjas horarias disponibles para el mismo médico o fecha.

2. **Given** todas las franjas del médico seleccionado en la fecha elegida están ocupadas,
   **When** el paciente intenta confirmar cualquier franja,
   **Then** el sistema indica que no hay disponibilidad para ese médico en esa fecha
   y retorna una lista de franjas alternativas vacía con un mensaje explicativo.
   *(Nota: la sugerencia de fechas alternativas y médicos alternativos es alcance de una épica
   futura; este US solo garantiza el mensaje de error claro y la lista de franjas del mismo
   médico en la misma fecha.)*

---

### User Story 3 - Consulta de Disponibilidad de Médicos y Horarios (Priority: P3)

Como paciente, quiero ver qué médicos están disponibles y en qué franjas horarias, para poder tomar
una decisión informada antes de confirmar mi reserva.

**Why this priority**: Es prerrequisito funcional de US1 y US2. Sin visibilidad de disponibilidad
el paciente no puede seleccionar médico ni franja. Sin embargo, se ubica en P3 porque la lógica de
negocio central (reservar / bloquear) es más crítica.

**Independent Test**: Testeable consultando la disponibilidad de un médico; el resultado debe mostrar
correctamente las franjas libres y ocupadas sin necesidad de llegar al paso de confirmación.

**Acceptance Scenarios**:

1. **Given** el paciente accede al módulo de reservas,
   **When** selecciona una especialidad y/o médico y una fecha,
   **Then** el sistema muestra las franjas horarias disponibles (libres) diferenciadas visualmente
   de las franjas no disponibles (ocupadas).

2. **Given** no existen franjas disponibles para el médico seleccionado en la fecha consultada,
   **When** el paciente consulta la disponibilidad,
   **Then** el sistema retorna una lista de franjas vacía para esa fecha.
   *(Nota: proponer la próxima fecha con cupos disponibles es alcance de una épica futura;
   el cliente puede consultar otras fechas usando el mismo endpoint con parámetro `fecha`.)*

---

### Edge Cases

- ¿Qué ocurre si dos pacientes intentan reservar la misma franja simultáneamente? El sistema DEBE
  garantizar que solo uno de ellos logre la reserva (control de concurrencia).
- ¿Qué ocurre si el servicio de WhatsApp no está disponible al momento de confirmar? La cita DEBE
  quedar registrada igualmente. En esta épica, el envío es sincrónico con stub; el reintento
  asíncrono es **deuda técnica planificada para una épica futura** — los campos `intentos` y
  `ultimoIntento` de `NotificacionWhatsApp` están modelados para soportarlo.
- ¿Qué ocurre si el paciente cierra el navegador después de confirmar pero antes de recibir la
  confirmación visual? La reserva ya registrada en el sistema DEBE persistir.
- ¿Qué pasa si el médico cancela su disponibilidad después de que el paciente ya reservó? Fuera del
  alcance de este US (se gestiona en épica de gestión de agenda del médico).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema DEBE permitir al paciente consultar los médicos disponibles por especialidad.
- **FR-002**: El sistema DEBE mostrar las franjas horarias de un médico diferenciando las disponibles
  de las ocupadas.
- **FR-003**: El sistema DEBE permitir al paciente seleccionar médico, fecha y franja horaria para
  iniciar una reserva.
- **FR-004**: El sistema DEBE registrar la cita y marcar la franja como ocupada de forma atómica al
  confirmar la reserva, garantizando que dos reservas simultáneas no puedan ocupar la misma franja.
- **FR-005**: El sistema DEBE enviar una notificación de confirmación al número de WhatsApp del
  paciente inmediatamente después de registrar la cita.
- **FR-006**: El sistema DEBE rechazar la confirmación de una franja ya ocupada, mostrar un mensaje
  explicativo y ofrecer franjas alternativas disponibles.
- **FR-007**: El sistema DEBE estar disponible para recibir reservas las 24 horas del día, los 7
  días de la semana, sin requerir intervención manual.
- **FR-008**: La notificación de WhatsApp DEBE incluir: nombre del médico, especialidad, fecha,
  hora, y número de referencia de la cita.
- **FR-009**: El sistema DEBE permitir consultar la disponibilidad de un médico para cualquier
  fecha mediante un parámetro de fecha en la petición; la navegación entre fechas (avanzar/retroceder
  días) es responsabilidad del cliente consumidor de la API.

### Key Entities

- **Paciente**: Persona que solicita la cita. Posee identificación, nombre, número de WhatsApp y
  datos de contacto. Ya debe estar registrado en el sistema.
- **Médico**: Profesional de salud con especialidad, nombre y disponibilidad configurada en el
  sistema.
- **FranjaHoraria**: Intervalo de tiempo en la agenda de un médico (ej. 09:00–09:30). Tiene estado:
  `DISPONIBLE` u `OCUPADA`. Pertenece a un médico en una fecha específica.
- **Cita**: Reserva confirmada entre un paciente y un médico en una franja horaria. Tiene estado
  (`CONFIRMADA`, `CANCELADA`, `COMPLETADA`), número de referencia único y timestamp de creación.
- **NotificacionWhatsApp**: Registro de los intentos de envío de confirmación al paciente, con
  estado y timestamp del último intento.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Un paciente puede completar el flujo de reserva de principio a fin en menos de
  3 minutos.
- **SC-002**: El sistema acepta y procesa reservas las 24 horas del día, los 7 días de la semana,
  con disponibilidad ≥ 99.5% mensual.
- **SC-003**: La notificación de confirmación por WhatsApp llega al paciente en menos de 60 segundos
  tras confirmar la reserva.
- **SC-004**: El 100% de las franjas reservadas se muestran correctamente como no disponibles para
  otros pacientes inmediatamente después de la confirmación.
- **SC-005**: El sistema garantiza que no se produzcan reservas duplicadas en la misma franja (tasa
  de conflictos de concurrencia = 0%).
- **SC-006**: El 90% de los pacientes completa el flujo de reserva exitosamente en el primer intento
  sin necesidad de asistencia.

## Assumptions

- El paciente ya está autenticado e identificado en el sistema (registro/login previo fuera del
  alcance de este US).
- El paciente tiene un número de WhatsApp activo y registrado en su perfil del sistema.
- La integración con WhatsApp Business API es provista por un servicio externo ya disponible en la
  plataforma; la responsabilidad de este US es invocarla correctamente.
- Los médicos ya tienen sus franjas horarias configuradas en el sistema por un administrador
  (gestión de agenda del médico fuera del alcance de este US).
- El "horario de atención telefónica" mencionado en la historia de usuario es una restricción del
  canal telefónico, no del sistema web; el sistema web opera 24/7 sin restricciones.
- Una franja horaria estándar tiene duración de 30 minutos (configurable a futuro, no en este US).
- El alcance de este US es la reserva de citas; la cancelación y modificación pertenecen a épicas
  separadas.
