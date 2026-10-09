# Arquitectura del MVP --- Sistema de Gestión de Casos VIF

**Proyecto:** Sistema Web de Gestión de Casos VIF para las Comisarías de
Familia de Neiva\
**Versión:** 1.0 --- Propuesta para revisión del equipo\
**Fecha:** 9 de octubre de 2026\
**Estado:** Propuesta; la aprobación del equipo debe confirmarse en
Jira.

## 1. Objetivo

Definir una arquitectura sencilla para el MVP que separe interfaz,
lógica de negocio y persistencia. El expediente VIF será el elemento
central. Actuaciones, documentos, personas, seguimiento e incidentes se
relacionan con él según corresponda.

## 2. Diagrama de arquitectura

``` mermaid
flowchart TD
    U[Usuario] --> FE[Frontend React]
    FE -->|REST / JSON por HTTP(S)| API[API REST - Spring Boot]
    API --> SEC[Spring Security]
    API --> CTRL[Controllers]
    CTRL --> SVC[Services y reglas de negocio]
    SVC --> REP[Spring Data JPA]
    REP --> DB[(PostgreSQL)]
    SVC --> FS[Almacenamiento de archivos del MVP]
    FS -. metadatos .-> DB
```

React no se conecta directamente a PostgreSQL. El backend autentica,
autoriza, valida reglas de negocio y accede a la base de datos.

## 3. Tecnologías

  -----------------------------------------------------------------------
  Componente              Tecnología              Responsabilidad
  ----------------------- ----------------------- -----------------------
  Frontend                React                   Interfaz, formularios,
                                                  navegación y
                                                  visualización

  Backend                 Java + Spring Boot      API REST, reglas de
                                                  negocio y seguridad

  Persistencia            Spring Data JPA /       Acceso a datos
                          Hibernate               

  Base de datos           PostgreSQL              Datos persistentes

  Comunicación            REST / JSON             Intercambio entre
                                                  frontend y backend

  Seguridad               Spring Security         Autenticación y
                                                  autorización

  Versionamiento          Git + GitHub            Código fuente

  Gestión                 Jira                    Sprints y tareas
  -----------------------------------------------------------------------

No se proponen microservicios, Kubernetes, Redis ni colas de mensajes
para el MVP, salvo que surja una necesidad demostrable.

## 4. Responsabilidades

### React

-   Mostrar login, dashboard, listado y detalle de expedientes.
-   Proveer formularios para personas, actuaciones, documentos,
    seguimiento e incidentes.
-   Mostrar estado y línea de tiempo.
-   Consumir la API y presentar errores de forma comprensible.
-   No conectarse directamente a la base de datos ni ser la única
    barrera de autorización.

### Spring Boot

-   Exponer endpoints REST.
-   Validar entradas y aplicar reglas de negocio.
-   Autenticar usuarios y verificar permisos.
-   Aplicar el aislamiento por Comisaría en cada operación sobre
    expedientes.
-   Coordinar persistencia y manejo de errores.
-   Registrar trazabilidad básica de acciones relevantes.

### PostgreSQL

-   Persistir usuarios, comisarías, expedientes, personas, actuaciones y
    registros relacionados.
-   Mantener relaciones e integridad referencial.
-   Apoyar la prevención de duplicados mediante restricciones y
    validaciones.
-   Almacenar metadatos de documentos y archivos. El almacenamiento
    binario puede mantenerse separado.

## 5. Estructura inicial del backend

``` text
backend/src/main/java/<paquete-base>/
├── config/
├── security/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
├── mapper/
├── exception/
└── common/

backend/src/main/resources/
├── application.yml
└── db/migration/
```

-   `controller`: solicitudes HTTP; sin lógica de negocio compleja.
-   `service`: reglas de negocio y coordinación.
-   `repository`: acceso a datos con Spring Data JPA.
-   `entity`: entidades persistentes.
-   `dto`: contratos de entrada/salida.
-   `mapper`: conversión entre DTO y entidades.
-   `security`: autenticación y autorización.
-   `config`: configuración general.
-   `exception`: manejo centralizado de errores.
-   `db/migration`: migraciones versionadas si se adopta Flyway u otra
    herramienta.

## 6. Estructura inicial del frontend

``` text
frontend/src/
├── assets/
├── components/
├── layouts/
├── pages/
│   ├── Login/
│   ├── Dashboard/
│   ├── Casos/
│   ├── Expediente/
│   ├── Personas/
│   ├── Documentos/
│   ├── Seguimiento/
│   └── Incidentes/
├── routes/
├── services/api/
├── context/
├── hooks/
├── utils/
└── App.jsx
```

La estructura y extensiones se adaptarán al repositorio real y a la
decisión del equipo de usar JavaScript o TypeScript.

## 7. Entidades principales

  -----------------------------------------------------------------------
  Entidad                             Propósito
  ----------------------------------- -----------------------------------
  `Comisaria`                         Identifica la Comisaría

  `Usuario`                           Cuenta, rol y Comisaría

  `CasoVIF`                           Expediente central, radicado,
                                      estado y fechas

  `Persona`                           Datos de las personas

  `CasoPersona`                       Relación persona-expediente y
                                      calidad

  `Actuacion`                         Actuaciones del expediente

  `Documento`                         Metadatos/referencia de documentos

  `Notificacion`                      Citaciones y notificaciones

  `EntidadExterna`                    Catálogo de entidades relacionadas

  `Oficio`                            Oficios asociados al expediente

  `Audiencia`                         Datos de audiencia

  `Prueba`                            Metadatos/referencia de archivos

  `Seguimiento`                       Registros de seguimiento

  `Incidente`                         Incidente relacionado con el
                                      expediente; puede repetirse

  `IncidenteActuacion`                Actuaciones propias del incidente

  `Auditoria`                         Trazabilidad básica
  -----------------------------------------------------------------------

Esta lista es inicial, no un modelo entidad-relación definitivo.
Atributos, cardinalidades y restricciones deben definirse en la
actividad de diseño de base de datos.

## 8. Flujo de comunicación

Ejemplo: crear un expediente.

1.  React envía `POST /api/casos` con JSON.
2.  Spring Security comprueba la identidad y los permisos.
3.  `CasoController` valida el formato de entrada.
4.  `CasoService` aplica reglas de negocio y comprueba la Comisaría
    autorizada.
5.  `CasoRepository` persiste el expediente en PostgreSQL.
6.  Spring Boot devuelve una respuesta HTTP y React actualiza la vista.

Endpoints iniciales propuestos: - `POST /api/auth/login` -
`GET /api/casos` - `POST /api/casos` - `GET /api/casos/{id}` -
`POST /api/casos/{id}/personas` - `GET /api/casos/{id}/actuaciones` -
`POST /api/casos/{id}/actuaciones` - `GET /api/casos/{id}/incidentes` -
`POST /api/casos/{id}/incidentes`

## 9. Autenticación y autorización

1.  Las credenciales se validan en el backend.
2.  Las contraseñas se guardan con hash adaptativo seguro; nunca en
    texto plano ni con cifrado reversible.
3.  Spring Security protege los endpoints privados.
4.  Cada usuario se asocia a una Comisaría y a un rol/permisos.
5.  Toda operación sobre expedientes verifica en el backend que el
    usuario tenga acceso a la Comisaría del caso.
6.  Ocultar opciones en React no sustituye la autorización del backend.
7.  Los secretos no se guardan en Git; se usan variables de entorno o
    configuración local excluida.
8.  Para la demo se usan datos ficticios. No se cargan expedientes
    reales sin autorización y controles adecuados.

**Pendiente de decisión:** elegir un solo mecanismo de sesión (por
ejemplo, sesión de servidor o token) y documentar almacenamiento,
expiración, logout y protección de credenciales. No se fija JWT como
decisión definitiva sin revisar el repositorio actual.

## 10. Decisiones arquitectónicas propuestas

  -----------------------------------------------------------------------
  ID                      Decisión                Motivo
  ----------------------- ----------------------- -----------------------
  DA-01                   Monolito modular con    Menor complejidad
                          Spring Boot             operativa para el MVP

  DA-02                   API REST entre React y  Separación de interfaz
                          Spring Boot             y lógica

  DA-03                   PostgreSQL              Persistencia relacional

  DA-04                   `CasoVIF` como entidad  Organiza el expediente
                          central                 

  DA-05                   Incidente relacionado y Puede haber varios
                          repetible               incidentes por
                                                  expediente

  DA-06                   Autorización en backend No depender del
                                                  frontend

  DA-07                   Actuaciones y           Un documento no
                          documentos separados    equivale necesariamente
                                                  a una etapa

  DA-08                   No fijar 27 pasos       El inventario
                          rígidos sin validación  documental no prueba
                                                  una secuencia
                                                  obligatoria
  -----------------------------------------------------------------------

## 11. Criterios de aceptación de la actividad

-   [x] Diagrama de arquitectura propuesto.
-   [x] Tecnologías del MVP documentadas.
-   [x] Responsabilidades de React, Spring Boot y PostgreSQL descritas.
-   [x] Estructura inicial del backend propuesta.
-   [x] Estructura inicial del frontend propuesta.
-   [x] Entidades principales identificadas.
-   [x] Flujo Frontend → Backend → base de datos documentado.
-   [x] Estrategia inicial de autenticación, autorización y aislamiento
    por Comisaría definida.
-   [ ] Aprobación de la arquitectura monolítica modular por Fernando,
    José y Miguel.
-   [ ] Archivo incorporado al repositorio y ajustado a la estructura
    real.

Los dos últimos puntos requieren acciones del equipo y no se consideran
terminados solo por redactar el documento.

## 12. Pasos para cerrar la tarea en Jira

1.  Revisar el documento entre Fernando, José y Miguel.
2.  Confirmar JavaScript/TypeScript, estructura de paquetes y mecanismo
    de sesión.
3.  Ajustar la propuesta al repositorio actual.
4.  Guardar este archivo como `README_ARQUITECTURA.md` en la raíz del
    repositorio o en `docs/`.
5.  Enlazar el archivo o el commit en Jira y registrar la aprobación del
    equipo.
6.  Cerrar la tarea cuando el archivo esté en GitHub y las decisiones
    pendientes estén confirmadas.

**Nota:** esta es una propuesta técnica para el MVP académico, no una
autorización para desplegar el sistema con información real. Los
requisitos institucionales, jurídicos, de protección de datos y de
gestión documental deben validarse antes de un uso productivo.
