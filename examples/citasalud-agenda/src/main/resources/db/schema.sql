CREATE TABLE IF NOT EXISTS medicos (
    id          UUID PRIMARY KEY,
    nombre      VARCHAR(200) NOT NULL,
    especialidad VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS pacientes (
    id               UUID PRIMARY KEY,
    nombre           VARCHAR(200) NOT NULL,
    numero_whatsapp  VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS franjas_horarias (
    id          UUID PRIMARY KEY,
    medico_id   UUID         NOT NULL REFERENCES medicos(id),
    fecha       DATE         NOT NULL,
    hora_inicio TIME         NOT NULL,
    hora_fin    TIME         NOT NULL,
    estado      VARCHAR(20)  NOT NULL DEFAULT 'DISPONIBLE',
    version     BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT chk_estado_franja CHECK (estado IN ('DISPONIBLE', 'OCUPADA'))
);

CREATE TABLE IF NOT EXISTS citas (
    id                UUID PRIMARY KEY,
    paciente_id       UUID         NOT NULL REFERENCES pacientes(id),
    medico_id         UUID         NOT NULL REFERENCES medicos(id),
    franja_horaria_id UUID         NOT NULL REFERENCES franjas_horarias(id),
    numero_referencia VARCHAR(50)  NOT NULL UNIQUE,
    estado            VARCHAR(20)  NOT NULL DEFAULT 'CONFIRMADA',
    creada_en         TIMESTAMP    NOT NULL,
    CONSTRAINT chk_estado_cita CHECK (estado IN ('CONFIRMADA', 'CANCELADA', 'COMPLETADA'))
);

CREATE TABLE IF NOT EXISTS notificaciones_whatsapp (
    id             UUID PRIMARY KEY,
    cita_id        UUID         NOT NULL REFERENCES citas(id),
    destinatario   VARCHAR(20),
    estado         VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    intentos       INT          NOT NULL DEFAULT 0,
    ultimo_intento TIMESTAMP,
    mensaje        TEXT,
    CONSTRAINT chk_estado_notificacion CHECK (estado IN ('PENDIENTE', 'ENVIADA', 'FALLIDA'))
);
