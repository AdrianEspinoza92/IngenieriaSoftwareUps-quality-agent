# Quality & Governance Agent — Unidad 3 (CitaSalud)

Tercer agente del curso *Ingeniería de Software* (Maestría en Software, UPS).
Verifica **código Spring Boot ya escrito** contra tres pilares y **bloquea** lo que no
cumple. Mismo ADN que el **Discovery Agent** (U1) y el **Agile Delivery Team** (U2):
constitución, skill, comandos, subagentes y un **hook que bloquea**.

```
Discovery (U1)  →  Delivery Team (U2)  →  Quality Agent (U3)  →  paquete listo
   descubre            planifica            verifica el código       ↑
                                            ✗ no cumple → vuelve al equipo
```

Este repositorio contiene **el agente** (en la raíz) y **un proyecto Spring Boot real de
ejemplo** ya verificado en `examples/citasalud-agenda/` — la implementación de la historia
**US-01 · Reserva de cita online 24/7** bajo Clean Architecture, BDD y API First.

## Los tres pilares

| Pilar | Pasa si… | Cómo se mide |
|------|----------|--------------|
| **Pruebas** | todas pasan **y** cobertura ≥ 80% | `./gradlew test jacocoTestReport` + JaCoCo XML |
| **Seguridad** | 0 críticas **y** 0 secretos | **Semgrep MCP** + grep de secretos |
| **Criterios** | cada `FR-xxx` del spec cubierto | cruce `spec.md` (Spec Kit) ↔ pruebas |

El veredicto lo decide el **gate determinista** `.claude/hooks/quality-gate.py`, no el modelo.
El gate custodia el archivo `verification.json`: si los tres pilares pasan → **exit 0**
(APROBADO); si alguno falla → **exit 2** (BLOQUEADO), con el motivo por pilar.

## Estructura

```
citasalud-agenda/  (raíz = el agente)
├── CLAUDE.md                     # constitución (tres pilares, "el gate manda")
├── README.md                     # este archivo
├── INFORME.md                    # informe/reflexión de la práctica (1 pág.)
├── .mcp.json                     # conexión MCP (Semgrep) — ver "MCP" abajo
├── .claude/
│   ├── settings.json             # hook PreToolUse → quality-gate.py · enabledMcpjsonServers: semgrep
│   ├── agents/                   # auditor + security-reviewer
│   ├── commands/quality/         # verify · review-api · review-architecture · generate-report
│   ├── commands/demo-gate.md     # /demo-gate — demo grabable del bloqueo→resolución
│   ├── hooks/quality-gate.py     # EL GATE (determinista)
│   ├── scripts/build-report.py   # reporte HTML (paleta U3)
│   └── skills/quality/SKILL.md   # Definition of Done, umbrales, formatos
└── examples/
    └── citasalud-agenda/         # PROYECTO Spring Boot verificado (US-01 reserva de cita)
        ├── build.gradle · settings.gradle · gradlew
        ├── specs/001-reserva-cita-online/spec.md   # Spec Kit: FRs, escenarios (CRITERIOS)
        ├── .specify/memory/constitution.md         # Spec Kit: umbrales (cobertura ≥ 80%)
        ├── src/main/java/... · src/test/java/...    # código + pruebas (3 capas BDD)
        ├── demo-gate.sh          # demo del gate por terminal (plan B, sin Claude)
        └── quality-output/       # lo que GENERA el agente: verification.json + report.html
```

> **Entrada = la ruta del proyecto.** Se pasa la **raíz** de un proyecto Spring Boot (con
> `build.gradle`, `src/` y los specs de **Spec Kit** en `specs/`). **No hay carpeta `code/`
> ni `acceptance.json`**: los criterios se leen del `spec.md`. El agente escribe sus
> resultados dentro del proyecto, en `quality-output/`.

## De dónde salen los criterios (Spec Kit, no `acceptance.json`)

De los tres pilares, dos se derivan del proyecto: **Pruebas** (Gradle + JaCoCo) y
**Seguridad** (Semgrep). El tercero, **Criterios**, necesita saber *qué se prometió* — y eso
no está en el código. Su valor real es cazar la **omisión silenciosa**: un requisito que **no
tiene ninguna prueba** aunque todo lo demás esté en verde (la cobertura no lo ve — mide
líneas ejecutadas, no requisitos cubiertos).

Como el proyecto usa **Spec Kit**, esa fuente de verdad ya existe: el `spec.md` que genera
`/speckit.specify`. El agente lee los **Functional Requirements** (`FR-001…FR-009`), los
**Acceptance Scenarios** y los **Edge Cases** de `examples/citasalud-agenda/specs/…/spec.md`,
y verifica que cada uno tenga una prueba. Esto cierra el bucle de SDD: el Quality Agent es la
fase **verify** que confirma que el código cumple el spec.

## Para qué se usa el MCP (Semgrep)

El **pilar de Seguridad** se apoya en el servidor **MCP oficial de Semgrep** (`semgrep-mcp`).
El agente lo usa para **escanear el código Java/Spring y reunir las vulnerabilidades**
(`semgrep_scan`, `security_check`), que luego pueblan el bloque `security` de
`verification.json`.

> **El MCP reúne la evidencia; el gate decide sobre ella.** El pasa/bloquea NO depende de un
> servicio externo ni de un modelo: el gate local y determinista mira los números
> (`critical`, `secrets`) y dicta. El MCP son los *sentidos* del agente; el gate es el *juez*.

Configuración (en `.mcp.json`, project-scoped y versionable):

```json
{ "mcpServers": {
    "semgrep": { "command": "uvx", "args": ["semgrep-mcp"],
                 "env": { "SEMGREP_APP_TOKEN": "${SEMGREP_APP_TOKEN}" } } } }
```

- Requiere `uv`/`uvx` (o Docker: `ghcr.io/semgrep/mcp -t stdio`).
- `SEMGREP_APP_TOKEN` es **opcional**: sin token, Semgrep usa sus reglas locales; con token,
  se conecta a la plataforma AppSec. Expórtalo en tu shell (no lo escribas en el archivo).
- Claude Code te pedirá **aprobar** el servidor del `.mcp.json` la primera vez.

## Cómo se usa con Claude Code

```bash
claude                                              # aprueba el MCP de Semgrep al inicio

/quality:verify        examples/citasalud-agenda    # verifica → quality-output/verification.json
/quality:generate-report examples/citasalud-agenda  # dashboard HTML del veredicto
```

Cuando el agente escribe `examples/citasalud-agenda/quality-output/verification.json`, el
**gate** se dispara: **APROBADO** (exit 0) si los tres pilares pasan; **BLOQUEADO** (exit 2)
si alguno falla, y el trabajo vuelve al equipo.

## Demo del gate (evidencia bloqueo → resolución)

- **Como actúo yo (Claude), grabable:** escribe en el chat `/demo-gate`. Intentaré registrar
  el `verification.json` con **FR-006 sin prueba** → el hook me **bloquea** (exit 2) y lo
  reporto; luego lo **resuelvo** (FR-006 cubierto, 39/39, cobertura 95.3%) → **APROBADO**.
- **Por terminal (plan B, sin Claude):** `bash examples/citasalud-agenda/demo-gate.sh`.

## Resultado del ejemplo verificado

`examples/citasalud-agenda` **pasa** los tres pilares:

```
✓ PRUEBAS:   39 de 39 pasan; cobertura 95.3% ≥ 80%
✓ SEGURIDAD: 0 críticas · 0 secretos
✓ CRITERIOS: FR-001…FR-009 con prueba (incl. FR-004/FR-006 concurrencia)
→ APROBADO
```

A diferencia del ejemplo base del profesor —que trae un hueco deliberado en **FR-006**
(carrera de concurrencia sin prueba)—, aquí ese requisito **sí** está cubierto por
`ConcurrencyTest` y la reserva se hace de forma **atómica**. El demo reproduce ese hueco a
propósito para evidenciar el bloqueo y su resolución.
