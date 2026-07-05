#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# Demo del Quality Gate (Unidad 3) — evidencia bloqueo → resolución
#
#   1. BLOQUEO:     verification.json con FR-006 sin prueba (cobertura 79%,
#                   2 tests rojos)  ->  el gate real BLOQUEA  (exit 2)
#   2. RESOLUCIÓN:  verification.json real (39/39, 95.3%, FR-006 cubierto)
#                   ->  el gate real APRUEBA  (exit 0)
#
# El gate NO es del demo: es .claude/hooks/quality-gate.py del profe.
# Uso:  bash examples/citasalud-agenda/demo-gate.sh
# ---------------------------------------------------------------------------
set -u

HERE="$(cd "$(dirname "$0")" && pwd)"          # examples/citasalud-agenda
ROOT="$(cd "$HERE/../.." && pwd)"              # raíz del agente
GATE="$ROOT/.claude/hooks/quality-gate.py"
QO="$HERE/quality-output"
VJSON="$QO/verification.json"

pausa(){ read -r -p $'\n  [ENTER para continuar] '; }

echo "=========================================================="
echo "  DEMO — Quality Gate (Definition of Done, determinista)"
echo "  gate: .claude/hooks/quality-gate.py   proyecto: citasalud-agenda"
echo "=========================================================="
pausa

echo
echo ">> PASO 1 — Estado con un FR sin prueba (FR-006). Se espera BLOQUEO."
echo "   cp verification.blocked.json  ->  verification.json"
cp "$QO/verification.blocked.json" "$VJSON"
echo "   python3 quality-gate.py verification.json"
python3 "$GATE" "$VJSON"
echo "   >>> exit code: $?   (2 = BLOQUEADO)"
pausa

echo
echo ">> PASO 2 — Se resuelve: FR-006 con prueba, 39/39, cobertura 95.3%. Se espera APROBADO."
echo "   cp verification.approved.json  ->  verification.json"
cp "$QO/verification.approved.json" "$VJSON"
echo "   python3 quality-gate.py verification.json"
python3 "$GATE" "$VJSON"
echo "   >>> exit code: $?   (0 = APROBADO)"

echo
echo ">> Reporte HTML del veredicto final:"
python3 "$ROOT/.claude/scripts/build-report.py" "$VJSON"
echo
echo "  Fin del demo. verification.json queda en estado APROBADO."
