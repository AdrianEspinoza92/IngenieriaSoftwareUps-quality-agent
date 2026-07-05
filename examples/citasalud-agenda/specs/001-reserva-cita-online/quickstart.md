# Quickstart — Validación: Reserva de Cita en Línea 24/7

**Feature**: US-01 / Épica E-01
**Date**: 2026-06-27

Esta guía describe cómo validar que la feature funciona end-to-end. No incluye código de
implementación; para eso ver `tasks.md`. Para el modelo de datos ver `data-model.md`. Para el
contrato API ver `contracts/openapi.yml`.

---

## Prerequisitos

```bash
# Java 17+ instalado
java -version

# Gradle wrapper disponible
./gradlew --version

# (Opcional) curl o httpie para llamadas manuales
curl --version
```

> Los archivos `src/main/resources/db/schema.sql` y `src/main/resources/db/data.sql`
> se cargan automáticamente al iniciar la app (Spring Boot auto-configura `spring.sql.init`).
> Para pruebas, `src/test/resources/db/data-test.sql` se carga en H2 vía `application-test.yml`.

---

## 1. Build y pruebas

```bash
# Ejecutar todas las pruebas (unitarias + integración + BDD)
./gradlew test

# Generar reporte de cobertura JaCoCo
./gradlew jacocoTestReport

# Verificar umbrales de cobertura (falla si < 80%)
./gradlew jacocoTestCoverageVerification

# Build completo (incluye generación de código OpenAPI + pruebas + cobertura)
./gradlew build
```

**Reporte de cobertura**: `build/reports/jacoco/test/html/index.html`

---

## 2. Generación de código desde el contrato OpenAPI

```bash
# Generar stubs/interfaces/DTOs desde el contrato
./gradlew openApiGenerate

# Verificar que se generaron los archivos
ls build/generated/src/main/java/org/citasalud/api/
# Esperado: CitasApi.java, MedicosApi.java y directorio model/
```

---

## 3. Levantar el servidor local

```bash
./gradlew bootRun
# Servidor disponible en http://localhost:8080
```

---

## 4. Escenario US1 — Reserva exitosa (happy path)

### Paso 1: Listar médicos disponibles

```bash
curl -s http://localhost:8080/api/v1/medicos | jq .
```

**Resultado esperado**: Array con al menos un médico. Anotar el `id` del médico elegido.

### Paso 2: Consultar disponibilidad

```bash
MEDICO_ID="<id-del-medico>"
FECHA="2026-07-15"

curl -s "http://localhost:8080/api/v1/medicos/${MEDICO_ID}/disponibilidad?fecha=${FECHA}" | jq .
```

**Resultado esperado**: Objeto `DisponibilidadResponse` con `franjas` que incluyen al menos una
franja con `estado: "DISPONIBLE"`. Anotar el `id` de esa franja.

### Paso 3: Reservar la cita

```bash
PACIENTE_ID="<id-del-paciente-de-prueba>"
FRANJA_ID="<id-franja-disponible>"

curl -s -X POST http://localhost:8080/api/v1/citas \
  -H "Content-Type: application/json" \
  -d "{
    \"pacienteId\": \"${PACIENTE_ID}\",
    \"medicoId\": \"${MEDICO_ID}\",
    \"franjaHorariaId\": \"${FRANJA_ID}\",
    \"fecha\": \"${FECHA}\"
  }" | jq .
```

**Resultado esperado**:
- HTTP `201 Created`
- Body con `estado: "CONFIRMADA"` y `numeroReferencia` en formato `CIT-YYYY-NNNNN`
- WhatsApp notification enviada al paciente (verificar logs en consola)

### Paso 4: Verificar que la franja está ocupada

```bash
curl -s "http://localhost:8080/api/v1/medicos/${MEDICO_ID}/disponibilidad?fecha=${FECHA}" | jq '.franjas[] | select(.id == "'${FRANJA_ID}'")'
```

**Resultado esperado**: La franja aparece con `estado: "OCUPADA"`.

---

## 5. Escenario US2 — Franja ya ocupada (conflicto)

### Paso 1: Intentar reservar la misma franja nuevamente

```bash
curl -s -X POST http://localhost:8080/api/v1/citas \
  -H "Content-Type: application/json" \
  -d "{
    \"pacienteId\": \"<otro-paciente-id>\",
    \"medicoId\": \"${MEDICO_ID}\",
    \"franjaHorariaId\": \"${FRANJA_ID}\",
    \"fecha\": \"${FECHA}\"
  }" | jq .
```

**Resultado esperado**:
- HTTP `409 Conflict`
- Body con `codigo: "FRANJA_OCUPADA"` y mensaje descriptivo
- `franjasAlternativas` con otras franjas disponibles del mismo médico y fecha

---

## 6. Escenario US3 — Sin disponibilidad (fecha sin franjas)

```bash
curl -s "http://localhost:8080/api/v1/medicos/${MEDICO_ID}/disponibilidad?fecha=2099-01-01" | jq .
```

**Resultado esperado**: `franjas: []` (lista vacía) — el médico no tiene agenda para esa fecha.

---

## 7. Validar pruebas BDD (Cucumber)

```bash
./gradlew test --tests "*CucumberRunner*"
```

**Resultado esperado**: Los tres feature scenarios de `reserva_cita.feature` deben aparecer como
`PASSED`. El reporte Cucumber HTML se genera en `build/reports/tests/test/`.

---

## 8. Constitution Check final

| Principio | Verificación |
|-----------|-------------|
| I. Clean Architecture | `./gradlew test` sin imports de Spring en `org.citasalud.domain.*` |
| II. BDD | Cucumber report: todos los scenarios en verde |
| III. SOLID/YAGNI/DRY | Code review del PR — sin duplicación, sin clases con múltiples responsabilidades |
| IV. API First | `ls specs/001-reserva-cita-online/contracts/openapi.yml` — existe antes del código |
| V. JaCoCo | `./gradlew jacocoTestCoverageVerification` — build exitoso |
