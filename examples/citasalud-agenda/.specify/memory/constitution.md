<!-- Sync Impact Report
Version change: N/A → 1.0.0 (initial ratification)
Added sections:
  - Core Principles (5 principles)
    1. I. Arquitectura Limpia (Clean Architecture — Robert C. Martin)
    2. II. Pruebas BDD (Behavior-Driven Development)
    3. III. Buenas Prácticas de Programación (SOLID, YAGNI, DRY)
    4. IV. API First con OpenAPI y openapi-generator
    5. V. Métricas de Calidad con JaCoCo
  - Estándares de Calidad y Stack Tecnológico
  - Flujo de Desarrollo
  - Governance
Modified principles: none (initial creation)
Templates requiring updates:
  - .specify/templates/plan-template.md ✅ updated — Constitution Check gates added
  - .specify/templates/spec-template.md ✅ aligned — BDD Given/When/Then already present; API First note added
  - .specify/templates/tasks-template.md ✅ updated — tests marked REQUIRED (BDD NON-NEGOTIABLE)
Deferred TODOs: none
-->

# CItaSalud Constitution

## Core Principles

### I. Arquitectura Limpia (NON-NEGOTIABLE)

La arquitectura del proyecto DEBE seguir los principios de Clean Architecture definidos por Robert C. Martin:

- El sistema DEBE organizarse en capas concéntricas: **Entidades** (reglas de negocio empresariales),
  **Casos de Uso** (reglas de negocio de aplicación), **Adaptadores de Interfaz** (controladores,
  presentadores, gateways) e **Infraestructura** (frameworks, DB, UI, agencias externas).
- La **Regla de Dependencia** es absoluta: las dependencias de código fuente DEBEN apuntar únicamente
  hacia adentro. Las capas internas NO DEBEN conocer nada sobre las capas externas.
- La lógica de negocio (Entidades y Casos de Uso) DEBE ser independiente de frameworks, UI, base de
  datos y cualquier agencia externa.
- Los límites arquitectónicos DEBEN cruzarse usando **Interfaces (Puertos)** e implementaciones
  (Adaptadores), aplicando el patrón Ports & Adapters.
- Cada Caso de Uso DEBE representar una única operación de negocio y tener una única razón para cambiar.
- Las Entidades encapsulan las reglas de negocio más críticas y DEBEN ser los objetos más estables del
  sistema — sin dependencias hacia afuera.

**Rationale**: La independencia de capas garantiza que el negocio pueda probarse y evolucionar sin
atarse a decisiones tecnológicas. Permite reemplazar frameworks y bases de datos sin afectar la lógica
central, y facilita la testabilidad de cada capa en aislamiento.

### II. Pruebas BDD (NON-NEGOTIABLE)

Toda funcionalidad DEBE ser desarrollada y verificada mediante Behavior-Driven Development (BDD):

- Las pruebas DEBEN seguir el formato **Given-When-Then** en todos los niveles sin excepción.
- Se DEBEN implementar las tres capas de prueba:
  - **Unitarias**: Prueban una sola unidad en aislamiento (Entidades, Casos de Uso, lógica de dominio).
    Usan mocks para dependencias externas.
  - **Integración**: Prueban la interacción entre componentes (repositorios + DB, controladores +
    servicios). Usan infraestructura real o embebida.
  - **Funcionales/Aceptación**: Validan el comportamiento end-to-end desde la perspectiva del usuario
    o consumidor de la API. Usan Cucumber con feature files.
- Los escenarios de aceptación DEBEN definirse en `spec.md` **antes** de comenzar la implementación.
- El ciclo DEBE ser: **Red → Green → Refactor** (las pruebas DEBEN fallar primero).
- El nombrado de los métodos de prueba DEBE describir comportamiento en lenguaje natural:
  `should_<resultado>_when_<condicion>`.
- Está PROHIBIDO crear pruebas que nunca fallen (pruebas triviales o siempre en verde desde el inicio).

**Rationale**: BDD alinea las pruebas con el comportamiento real del sistema, sirve como documentación
viva y garantiza que el software cumple con requisitos de negocio, no solo con especificaciones técnicas.

### III. Buenas Prácticas de Programación (NON-NEGOTIABLE)

El código del proyecto DEBE adherirse a los principios **SOLID**, **YAGNI** y **DRY**:

**SOLID**:

- **S** — Single Responsibility: Cada clase o módulo DEBE tener una única razón para cambiar.
- **O** — Open/Closed: El software DEBE estar abierto para extensión y cerrado para modificación.
  Se DEBEN preferir extensiones via herencia, composición o estrategias antes que modificar código
  existente.
- **L** — Liskov Substitution: Los objetos de subtipos DEBEN poder reemplazar instancias de su
  supertipo sin alterar la corrección del programa.
- **I** — Interface Segregation: Los clientes NO DEBEN depender de interfaces que no utilizan.
  Se DEBEN preferir múltiples interfaces específicas sobre una interfaz genérica.
- **D** — Dependency Inversion: Los módulos de alto nivel NO DEBEN depender de módulos de bajo nivel.
  Ambos DEBEN depender de abstracciones. Las abstracciones NO DEBEN depender de detalles.

**YAGNI** (You Aren't Gonna Need It):

- Solo se DEBE implementar funcionalidad cuando sea necesaria para el requerimiento actual documentado.
- Está PROHIBIDO agregar código especulativo para "posibles usos futuros" no requeridos.

**DRY** (Don't Repeat Yourself):

- Cada pieza de conocimiento DEBE tener una única representación autoritativa y no ambigua en el
  sistema.
- La duplicación de lógica, configuración o datos DEBE eliminarse mediante abstracción apropiada.
  Tres o más instancias de lógica idéntica DEBEN ser abstraídas.

**Rationale**: Estos principios reducen la deuda técnica, facilitan el mantenimiento y permiten que el
código evolucione de forma sostenible. YAGNI evita el sobreingeniería; DRY evita inconsistencias;
SOLID garantiza extensibilidad.

### IV. API First con OpenAPI (NON-NEGOTIABLE)

El diseño de APIs DEBE seguir el enfoque **API First**:

- El contrato **OpenAPI 3.x** es la única fuente de verdad para todas las APIs REST del proyecto.
  DEBE existir y ser revisado **antes** de cualquier implementación.
- Los contratos OpenAPI DEBEN ubicarse en `specs/[###-feature-name]/contracts/openapi.yml`.
- Se DEBE utilizar **openapi-generator** (plugin de Gradle: `openapi-generator-gradle-plugin`) para
  generar los stubs del servidor, interfaces de controladores y DTOs desde el contrato.
- Está PROHIBIDO implementar manualmente endpoints, DTOs o interfaces que pueden generarse desde el
  contrato OpenAPI.
- Los contratos DEBEN incluir: esquemas completos de request/response, códigos de error estándar con
  ejemplos, y descripción de cada operación.
- Cualquier cambio en el contrato DEBE preceder al cambio en la implementación y DEBE conllevar un
  bump de versión en el campo `info.version` del contrato.
- Las pruebas de contrato (contract tests) DEBEN validar que la implementación cumple el contrato
  generado.

**Rationale**: API First garantiza que el diseño de la interfaz sea deliberado y consensuado. El uso
de openapi-generator elimina la brecha entre contrato e implementación, reduce errores y permite el
trabajo paralelo entre consumidores y proveedores de la API.

### V. Métricas de Calidad con JaCoCo (NON-NEGOTIABLE)

La calidad del código DEBE medirse y aplicarse mediante reglas de cobertura automáticas:

- **JaCoCo** DEBE estar configurado como plugin en el build de Gradle del proyecto
  (`id 'jacoco'` en `build.gradle`).
- **Cobertura por clase**: DEBE ser **> 80%** (instrucciones cubiertas por pruebas por cada clase).
- **Cobertura global del proyecto**: DEBE ser **≥ 80%** (cobertura de instrucciones total).
- La tarea `jacocoTestReport` DEBE ejecutarse automáticamente después de `test`.
- La tarea `jacocoTestCoverageVerification` DEBE configurarse con los umbrales definidos y DEBE hacer
  **fallar el build** si no se cumplen.
- Los reportes HTML de JaCoCo DEBEN generarse en `build/reports/jacoco/test/html/`.
- En el pipeline CI/CD, los reportes DEBEN publicarse como artefactos para revisión en cada PR.
- Las clases generadas por openapi-generator DEBEN excluirse de las métricas de cobertura.

**Rationale**: Las métricas de cobertura automatizadas garantizan que el mandato BDD se cumpla en
la práctica. El fallo automático del build previene regresiones en la calidad del código y hace que
los umbrales sean vinculantes, no sugerencias.

## Estándares de Calidad y Stack Tecnológico

El proyecto utiliza **Java** con **Gradle** como sistema de build. Todo el stack tecnológico DEBE
respetar los principios de esta constitución.

- **Lenguaje**: Java 17+ (LTS recomendado).
- **Build Tool**: Gradle con plugins obligatorios: `java`, `jacoco`, `openapi-generator-gradle-plugin`.
- **Framework de pruebas**: JUnit 5 (`junit-jupiter`). Mockito para dobles de prueba en unitarias.
- **BDD Framework**: Cucumber (`cucumber-java`, `cucumber-junit-platform-engine`) para pruebas de
  aceptación con feature files en `src/test/resources/features/`.
- **API**: Spring Boot Web (o equivalente); los controladores DEBEN implementar las interfaces
  generadas por openapi-generator.
- **Persistencia**: La capa de acceso a datos DEBEN implementarse como Adaptadores; el dominio
  solo conoce interfaces de repositorio (Puertos).
- **Reportes de cobertura**: JaCoCo — HTML en `build/reports/jacoco/`; XML para integración CI/CD.
- **Generación de código**: openapi-generator produce stubs en `build/generated/` (nunca editar
  manualmente).

## Flujo de Desarrollo

Todo desarrollo DEBE seguir este flujo sin excepciones:

1. **Definir el contrato OpenAPI** — El archivo `openapi.yml` DEBE existir y ser revisado por el
   equipo antes de cualquier línea de código de implementación.
2. **Ejecutar openapi-generator** — Generar stubs, interfaces y DTOs desde el contrato.
3. **Escribir escenarios BDD** — Definir los tests Given-When-Then (unitarios, integración,
   funcionales) que DEBEN estar en rojo (failing) antes de implementar.
4. **Implementar respetando Clean Architecture** — Lógica de negocio en capas internas; adaptadores
   en capas externas; cumplimiento de SOLID, YAGNI y DRY.
5. **Verificar cobertura** — El build DEBE pasar con todos los umbrales JaCoCo satisfechos. Los
   reportes DEBEN adjuntarse al PR.
6. **Code Review** — Todo PR DEBE verificar el "Constitution Check" del `plan.md` antes de merge.
   El incumplimiento de cualquier principio NON-NEGOTIABLE bloquea el merge.

El ciclo Red → Green → Refactor es OBLIGATORIO. No se aceptan PRs con pruebas que nunca fallaron.

## Governance

Esta constitución rige todas las decisiones de arquitectura, diseño y calidad del proyecto CItaSalud.
Ninguna otra directriz puede contradecirla sin una enmienda formal.

**Proceso de enmienda**:

- Cualquier modificación DEBE estar documentada con justificación técnica y de negocio.
- Los cambios que eliminen o redefinen principios NON-NEGOTIABLE (cambio MAYOR) DEBEN incluir un
  plan de migración aprobado por el equipo.
- Las versiones siguen Semantic Versioning: MAJOR (cambios incompatibles / eliminación de
  principios), MINOR (nuevas secciones o principios), PATCH (clarificaciones y redacción).

**Compliance**:

- Todo PR DEBE pasar el "Constitution Check" del `plan.md` antes de ser aprobado para merge.
- El incumplimiento de los principios NON-NEGOTIABLE bloquea el merge hasta ser resuelto.
- Los reportes JaCoCo DEBEN adjuntarse a cada PR que modifique código de producción.
- El pipeline CI/CD DEBE configurarse para hacer fallar el build si los umbrales de cobertura no se
  cumplen (principio V).

**Version**: 1.0.0 | **Ratified**: 2026-06-27 | **Last Amended**: 2026-06-27
