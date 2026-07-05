---
description: Demo grabable del Quality Gate — muestra el bloqueo (un FR sin prueba) y su resolución, actuando en vivo.
---

Ejecuta una demostración del **Quality Gate** para grabar. El objetivo es que en la
grabación se vea **cómo actúas tú (Claude)**: el gate bloqueando tu propio `Write` y luego
aprobándolo. Sigue EXACTAMENTE estos pasos, narrando en una línea antes de cada uno:

1. Di: **"Paso 1 — registro el veredicto con FR-006 sin prueba; el gate debe BLOQUEAR."**
2. Lee `examples/citasalud-agenda/quality-output/verification.blocked.json` y escribe su
   contenido **exacto** en `examples/citasalud-agenda/quality-output/verification.json` con la
   herramienta **Write**. El hook `PreToolUse` (`quality-gate.py`) interceptará ese Write.
3. El gate te **bloqueará (exit 2)**. Reporta textualmente el motivo por pilar
   (PRUEBAS / SEGURIDAD / CRITERIOS) tal como lo devuelve el gate. **No** reintentes ni
   fuerces la escritura: el bloqueo ES la evidencia. Explica en una frase por qué bloqueó
   (FR-006 sin prueba, 2 tests rojos, cobertura 79% < 80%).
4. Di: **"Paso 2 — lo resuelvo: FR-006 cubierto por ConcurrencyTest, reserva atómica,
   39/39 pruebas, cobertura 95.3%."**
5. Lee `examples/citasalud-agenda/quality-output/verification.approved.json` y escribe su
   contenido **exacto** en `examples/citasalud-agenda/quality-output/verification.json` con
   **Write**. Ahora el gate **APRUEBA (exit 0)** y la escritura se permite.
6. Ejecuta `python3 .claude/scripts/build-report.py examples/citasalud-agenda/quality-output/verification.json`
   y reporta el veredicto final **APROBADO** y la ruta del `report.html`.

Reglas: no modifiques ningún otro archivo; no edites el gate ni los `.json` fuente; no
uses `cp` ni Bash para escribir `verification.json` (debe ser con la herramienta **Write**,
para que el hook se dispare y se vea el bloqueo). Mantén la narración breve.
