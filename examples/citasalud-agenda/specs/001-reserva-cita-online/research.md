# Research: Reserva de Cita en Línea 24/7

**Feature**: US-01 / Épica E-01
**Date**: 2026-06-27

## Decisiones tecnológicas

### 1. Framework y lenguaje

**Decision**: Java 17 + Spring Boot 3.x

**Rationale**: El proyecto ya usa Java (Gradle, `group = 'org.citasalud'`). Spring Boot 3.x provee
el ecosistema maduro de Web MVC, Data JPA, y Testing necesario. Java 17 es LTS con soporte de records
y sealed classes útiles para el dominio.

**Alternatives considered**:
- Quarkus: más ligero, pero curva de aprendizaje adicional sin beneficio demostrado para esta escala.
- Spring Boot 2.x: descartado; fin de soporte en 2025.

---

### 2. Arquitectura: Clean Architecture + Hexagonal

**Decision**: Paquetes `domain`, `application`, `infrastructure` con sub-paquetes `port/in`,
`port/out`, `adapter/in/rest`, `adapter/out/persistence`, `adapter/out/whatsapp`.

**Rationale**: Cumple Principio I de la constitución. El dominio no tiene dependencias de Spring ni
JPA; los puertos son interfaces Java puras. Los adaptadores de infraestructura implementan los puertos.
Esto permite probar el dominio con JUnit 5 puro (sin Spring) y reemplazar la DB o WhatsApp sin tocar
el dominio.

**Alternatives considered**:
- Arquitectura por capas tradicional (controller/service/repository): descartada porque viola
  el Principio I (la capa de servicio termina dependiendo de Spring/JPA directamente).

---

### 3. Generación de API (API First)

**Decision**: `openapi-generator-gradle-plugin` con generador `spring`, modo `interfaceOnly = true`.

**Rationale**: Cumple Principio IV. El contrato `openapi.yml` se define primero. El plugin genera
interfaces Java + DTOs en `build/generated/`. El `CitasController` implementa la interfaz generada.
Ningún DTO de la capa REST se escribe manualmente.

**Configuration key**:
```groovy
openApiGenerate {
  generatorName = "spring"
  inputSpec     = "$rootDir/specs/001-reserva-cita-online/contracts/openapi.yml"
  outputDir     = "$buildDir/generated"
  apiPackage    = "org.citasalud.api"
  modelPackage  = "org.citasalud.api.model"
  configOptions = [interfaceOnly: "true", useSpringBoot3: "true"]
}
```

**Alternatives considered**:
- Swagger Annotations manuales: descartado; viola API First (la implementación dictaría el contrato).
- springdoc-openapi (generación desde código): descartado por la misma razón.

---

### 4. Control de concurrencia (doble reserva)

**Decision**: Bloqueo optimista con `@Version` en `FranjaHorariaEntity` + transacción en el
Use Case de reserva.

**Rationale**: Evita deadlocks del bloqueo pesimista. Si dos solicitudes llegan simultáneamente para
la misma franja, la segunda lanza `OptimisticLockingFailureException`; el adaptador REST la mapea
a HTTP 409 con franjas alternativas. Cumple FR-004 (atomicidad) y SC-005 (tasa de conflictos = 0 %).

**Alternatives considered**:
- Bloqueo pesimista (`SELECT FOR UPDATE`): mayor contención y potencial de deadlocks con alta concurrencia.
- Constraint único de BD sin locking de aplicación: no suficiente porque la respuesta necesita incluir
  franjas alternativas, lo que requiere lógica de aplicación en el mismo flujo.

---

### 5. Notificación WhatsApp

**Decision**: Puerto de salida `NotificacionWhatsAppPort` en el dominio; implementación adaptadora
HTTP en `infrastructure/adapter/out/whatsapp/`. Envío asíncrono con reintentos para no bloquear la
respuesta de reserva.

**Rationale**: Desacopla el dominio de la implementación concreta (Meta Business API, Twilio, etc.).
Cumple el edge case: si WhatsApp no está disponible, la cita se registra igual y la notificación
se reintenta. Se asume que la integración HTTP con el proveedor ya existe en el proyecto o se
implementa como stub en esta épica.

**Alternatives considered**:
- Envío síncrono en el mismo hilo de reserva: descartado; un fallo de WhatsApp causaría rollback
  de la transacción de reserva, lo que sería incorrecto desde la perspectiva del negocio.

---

### 6. Base de datos

**Decision**: PostgreSQL para producción; H2 en modo embebido para pruebas de integración.

**Rationale**: PostgreSQL soporta el aislamiento transaccional necesario para el control de
concurrencia. H2 (modo MySQL/PostgreSQL-compatible) permite pruebas de integración rápidas sin
infraestructura externa. Spring Data JPA abstrae la diferencia entre ambos.

---

### 7. Pruebas BDD

**Decision**: Cucumber 7.x con `cucumber-junit-platform-engine` + JUnit 5. Feature files en
`src/test/resources/features/`. Step definitions en `src/test/java/org/citasalud/cucumber/steps/`.

**Rationale**: Cumple Principio II. Los feature files actúan como documentación viva y validan los
criterios de aceptación de la spec directamente. El runner de JUnit 5 Platform integra con el
reporte de JaCoCo sin configuración adicional.

**Feature file clave**: `reserva_cita.feature` con escenarios Given-When-Then derivados de US1, US2, US3.

---

### 8. JaCoCo

**Decision**: Plugin `jacoco` en Gradle con `jacocoTestReport` y `jacocoTestCoverageVerification`.
Exclusión de `org/citasalud/api/**` (clases generadas por openapi-generator).

**Configuration key**:
```groovy
jacocoTestCoverageVerification {
  violationRules {
    rule {
      limit { counter = 'INSTRUCTION'; value = 'COVEREDRATIO'; minimum = 0.80 }
    }
    rule {
      element = 'CLASS'
      limit { counter = 'INSTRUCTION'; value = 'COVEREDRATIO'; minimum = 0.80 }
      excludes = ['org.citasalud.api.*', 'org.citasalud.api.model.*']
    }
  }
}
```

**Rationale**: Cumple Principio V. Las clases generadas están fuera del control del equipo y no deben
penalizar las métricas. El build falla automáticamente si los umbrales no se cumplen.
