# Informe — Reserva de Cita en Línea 24/7 (CitaSalud)

**Asignatura:** Ingeniería de Software — Unidad 3
**Maestría en Software, Universidad Politécnica Salesiana (UPS)**
**Caso:** `citasalud-agenda` · US-01 *Reserva de cita online 24/7* (Épica E-01, 8 pts)
**Verificado por:** Quality & Governance Agent (U3) — tres pilares: **pruebas · seguridad · criterios**
**Stack:** Java 17 · Spring Boot 3 · Clean Architecture · Cucumber (BDD) · JaCoCo · API First (OpenAPI)
**Veredicto del gate:** APROBADO (39/39 pruebas · 95.3% cobertura · 0 críticas/secretos · FR-001…FR-009 con prueba)

---

## Reflexión

## 1. ¿Qué cambió en mi forma de "dar por terminado" el código cuando el veredicto lo decidió un gate determinista en vez de mi propio criterio?

Cambió el **quién decide** y, con ello, la honestidad del "terminado". Mi criterio personal es negociable bajo presión: "esto ya se ve bien", "el caso raro no va a pasar", "lo cubro después". Es un veredicto de opinión, y la opinión cede cuando quiero cerrar la tarea. El `quality-gate.py` no negocia: lee el `verification.json` generado sobre mi proyecto y compara números crudos contra umbrales —cobertura ≥ 80 % (JaCoCo), 0 críticas y 0 secretos (Semgrep), cada `FR-xxx` con prueba—. Sumado a que las 39 pruebas BDD en tres capas (unitarias, integración, funcionales Cucumber) tienen que estar en verde, "terminado" pasó a ser un **hecho binario y reproducible**: pasa o no pasa, y da igual quién lo mire o a qué hora.

En la práctica esto movió el momento del esfuerzo hacia adelante. Dejé de escribir código y *luego* preguntarme si estaba listo; empecé a escribirlo sabiendo que un juez ciego iba a medirlo. El control de concurrencia (evitar doble reserva de la misma franja) es el mejor ejemplo: con mi criterio lo habría dado por bueno tras una prueba feliz; el gate me obligó a mantener el `ConcurrencyTest` en verde y a subir la cobertura real hasta **95 %** (1061 de 1113 instrucciones), no porque yo lo sintiera suficiente, sino porque el umbral no se sentía nada. También dejé de confundir "compila y corre" con "está hecho". El efecto secundario más sano fue **quitarme la carga de defender mi propio juicio**: no tengo que convencer a nadie de que está listo; lo dice el build.

## 2. ¿Qué pilar me costó más dejar en verde —pruebas, seguridad o criterios—, y por qué?

El que más costó fue **pruebas**, y en concreto la cobertura exigida por el gate sobre las rutas que uno prefiere no mirar. Los **criterios** (`FR-001…FR-009` del `spec.md`) estaban acotados desde antes —escenarios Given-When-Then definidos antes de implementar—, así que traducirlos a features de Cucumber fue trabajo, no drama; el riesgo real ahí era la *omisión silenciosa* (un `FR-xxx` sin ninguna prueba), y bastó cruzar cada requisito contra un test para cerrarlo. **Seguridad**, con el escaneo automático de Semgrep vía MCP, señaló cosas puntuales y localizables (0 críticas, 0 secretos). Pero las pruebas fueron duras por una razón estructural: el 80 % no se llena con el camino feliz. Se llena con lo incómodo —la franja ya ocupada, la excepción `FranjaNoDisponibleException`, el fallo del adaptador de WhatsApp, la colisión concurrente (FR-004/FR-006)— que es justo lo que uno subestima. Cada punto de cobertura que faltaba era un caso que yo había decidido, tácitamente, no probar.

Además, mantener las **tres capas** en verde a la vez cuesta más que cualquiera por separado: una unitaria con Mockito puede pasar mientras la integración con H2 revienta por el `schema.sql`, y la funcional Cucumber puede fallar aunque las dos anteriores estén verdes. El pilar de pruebas es el único que no admite "casi": o el porcentaje llega y todo pasa, o el build es rojo. Seguridad y criterios permitían más matiz de interpretación; la cobertura, no.

La prueba concreta que más me costó dejar en verde fue el `ConcurrencyTest` sobre la reserva de franja: no basta con simularlo con hilos y ver que "generalmente" funciona, hay que forzar la condición de carrera real (dos hilos reservando la misma franja al mismo tiempo) y afirmar sobre el resultado determinista —una reserva gana, la otra recibe `FranjaOcupadaException`—. La primera versión de la prueba pasaba por casualidad (orden de ejecución favorable); tuve que rehacerla para que fallara de forma confiable si el bloqueo optimista no estaba bien implementado, y solo ahí el gate la aceptó como evidencia real del FR-004/FR-006.

## 3. ¿Para qué me serviría un gate de Definition of Done (y el escaneo automático de seguridad vía MCP) en mi equipo real?

Para lo mismo que sirvió aquí: **sacar la palabra "terminado" del terreno de la opinión y volverla un contrato ejecutable**. En un equipo real, el "está listo" cede bajo fecha de entrega —se mergea una historia con cobertura floja o con una vulnerabilidad conocida "porque ya la arreglamos en el siguiente sprint"— y eso vuelve como retrabajo, incidente en producción o una demo que no cumple. Un gate de DoD en CI (pruebas verdes en las tres capas + umbral de cobertura + criterios de aceptación verificados) hace que el código que no está hecho **simplemente no pase**, sin depender de que alguien tenga la disciplina, o el peso político, de frenar un merge.

Lo concreto que me llevo de este ejercicio es el propio mecanismo: un hook determinista (`quality-gate.py`) que lee un `verification.json` con evidencia trazable —archivo y línea por cada hallazgo— y decide con `exit 0` o `exit 2`, sin margen para que alguien lo reinterprete en el momento del merge. En un equipo real eso se traduce en un paso de CI que bloquea el PR mismo, no en un checklist que un revisor puede saltarse un viernes a las 6pm porque "ya quedamos de entregar hoy". La diferencia no es tener más reglas, es que la regla viva en un script que se ejecuta, no en la memoria o la buena voluntad de alguien.

El **escaneo de seguridad automático vía MCP** cierra el pilar que más fácilmente se salta la revisión humana: nadie audita a mano inyección, secretos hardcodeados o dependencias vulnerables en cada PR, pero un escáner conectado por MCP lo hace en cada cambio, de forma consistente y sin fatiga. Su mayor valor no es *bloquear*, sino el trabajo que fuerza *antes*: sabiendo que el gate va a medir cobertura y que el escáner va a leer el diff, uno escribe la prueba del caso raro y evita el patrón inseguro desde el inicio, no después de que alguien lo pida. Es la misma filosofía de gobernanza ejecutable de las unidades anteriores: **la calidad sostenida no depende de la voluntad individual bajo presión, sino de controles automáticos que la convierten en el camino por defecto.**

---

> **Síntesis:** el gate no me quitó criterio; me quitó la posibilidad de mentirme.
> "Terminado" dejó de ser algo que yo declaraba y pasó a ser algo que el build demostraba.
