# language: es
Característica: Reserva de cita en línea 24/7
  Como paciente
  Quiero reservar una cita médica en cualquier momento del día
  Para no tener que llamar en horario de atención telefónica

  Antecedentes:
    Dado el sistema tiene un médico con id "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
    Y el sistema tiene un paciente con id "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"

  @US1
  Escenario: Reserva exitosa de cita en franja disponible
    Dado la franja horaria "cccccccc-cccc-cccc-cccc-cccccccccc01" está disponible
    Cuando el paciente reserva la franja "cccccccc-cccc-cccc-cccc-cccccccccc01" con el médico "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa" para la fecha "2026-07-15"
    Entonces la cita queda registrada con estado "CONFIRMADA"
    Y la franja horaria "cccccccc-cccc-cccc-cccc-cccccccccc01" pasa a estado "OCUPADA"
    Y el paciente recibe una notificación de confirmación por WhatsApp

  @US2
  Escenario: Intento de reserva en franja ya ocupada
    Dado la franja horaria "cccccccc-cccc-cccc-cccc-cccccccccc03" está ocupada
    Cuando el paciente intenta reservar la franja "cccccccc-cccc-cccc-cccc-cccccccccc03" con el médico "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa" para la fecha "2026-07-15"
    Entonces el sistema rechaza la reserva con código "FRANJA_OCUPADA"

  @US3
  Escenario: Consulta de disponibilidad del médico
    Dado el médico "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa" tiene franjas para la fecha "2026-07-15"
    Cuando el paciente consulta la disponibilidad del médico "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa" para la fecha "2026-07-15"
    Entonces el sistema retorna las franjas horarias con sus estados
    Y al menos una franja tiene estado "DISPONIBLE"
