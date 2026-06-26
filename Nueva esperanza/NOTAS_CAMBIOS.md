# 📒 Notas de cambios — Proyecto "Nueva esperanza" (para estudiar la defensa)

> Documento de estudio. Explica **qué se cambió, por qué y cómo defenderlo**.
> La defensa es **individual**: si no sabes explicar o modificar el código, se asume que no participaste.

---

## Estado inicial encontrado (diagnóstico)

| Microservicio | ¿Compilaba? | Problema principal |
|---|---|---|
| `ms` (inventario) | ❌ No | Services vacíos/rotos; modelos con `@OneToOne` inválido; falta un import |
| `ms2` (ventas) | ❌ No | Símbolo faltante en `BoletaController` |
| `ms3` (bicicleta) | ❌ No | Falta dependencia `spring-hateoas` + errores en assemblers |
| `ms4` (persona) | ✅ Sí | Compilaba limpio |
| `gateway` | ✅ Sí | Compila, pero con typos `pridcates`/`pedicates` y `lb://` sin Eureka |

**Faltantes de rúbrica (todos los microservicios):** pruebas unitarias reales, Swagger/OpenAPI, Docker, README, configuración YAML con perfiles.

---

## 1. `ms` — Inventario (REPARADO ✅ compila)

**Dominio:** gestiona Productos, Repuestos y Servicios del taller. Es un servicio "fuente": otros lo consumen.

### Qué estaba roto
- `ProductoService` estaba a medio escribir: usaba una variable `webClient` inexistente, referenciaba `BoletaDTO` (que es de ventas) y llamaba getters que no existían (`getBoletas()`, `getBoletaId()`).
- `RepuestoService` y `ServicioService` estaban **completamente vacíos**, pero sus controllers llamaban ~7 métodos cada uno.
- Modelos `Repuesto` y `Servicio` tenían `@OneToOne(mappedBy=...)` sobre un campo `Integer` → mapeo JPA inválido (un `@OneToOne` debe apuntar a otra **entidad**, no a un número).
- `RepuestoController` usaba `@RequestParam` sin importarlo.

### Qué se hizo
1. **Se implementaron los 3 services** (`ProductoService`, `RepuestoService`, `ServicioService`) con el patrón CSR:
   - El **service orquesta la lógica** y convierte entidad → DTO (`convertirADTO`), para no exponer la entidad JPA directamente.
   - CRUD completo: listar, buscar por id, por nombre, por código de barras, sin-stock, guardar, eliminar.
   - **Reglas de negocio** en `guardar...()`: precio > 0 y stock ≥ 0 (lanzan `IllegalArgumentException`).
2. **Repositorios:** se agregaron *query methods* de Spring Data (derivados del nombre):
   - `findByNombreProductoContainingIgnoreCase`, `findByCodigoBarras`, `findByStock`.
   - Equivalentes para Repuesto (`findByStockRepuesto`) y Servicio.
3. **Modelos:** se eliminó el `@OneToOne` inválido; `boleta_id` queda como referencia floja (solo guarda el id de la boleta del MS de ventas, sin relación JPA local).
4. Se agregó el import `RequestParam` faltante.

### Cómo defenderlo (preguntas típicas)
- **"¿Por qué el service convierte a DTO?"** → Para no exponer la entidad de base de datos al cliente; el DTO controla qué campos salen y desacopla la API del modelo interno.
- **"¿Dónde están las reglas de negocio?"** → En el método `guardarProducto/Repuesto/Servicio`: valida precio y stock antes de persistir.
- **"¿Cómo busca por nombre sin escribir SQL?"** → Con *query methods* de Spring Data JPA: el nombre del método (`findByNombreProductoContainingIgnoreCase`) genera la consulta automáticamente.
- **"¿Por qué quitaste el `@OneToOne`?"** → Porque apuntaba a un `Integer`, no a una entidad. Entre microservicios no se comparten tablas; solo se guarda el `id` de la boleta como referencia y, si hace falta el detalle, se consulta por REST al MS de ventas.

---
## 2. `ms2` — Ventas (REPARADO ✅ compila)

**Dominio:** gestiona Boletas y Tipos de pago. Una boleta tiene un tipo de pago y listas de ids de empleados/productos/repuestos/servicios.

### Qué estaba roto
- `BoletaService` y `TipoPagoService` estaban **vacíos**.
- `BoletaController` usaba `@RequestBody` sin importarlo → no compilaba.

### Qué se hizo
1. **`BoletaService` completo:** CRUD + `convertirADTO` (mapea `total`→`precio`, y el nombre del tipo de pago).
2. **Comunicación REST entre microservicios** (clave para la rúbrica) en `agregarProducto(boletaId, productoId)`:
   - Antes de agregar el producto a la boleta, **consulta por WebClient al ms de inventario** (`GET /api/v1/productos/{id}`).
   - Si el inventario responde 4xx, se traduce con `.onStatus(...)` a un error de negocio claro (manejo de error remoto).
   - La URL del inventario es configurable: `@Value("${ms.inventario.url:http://localhost:8081}")`.
   - Se creó un `ProductoDTO` local solo para deserializar esa respuesta.
3. **`TipoPagoService` completo:** CRUD + DTO (incluye la lista de ids de boletas asociadas).
4. Reglas de negocio: total ≥ 0 en boleta; tipo no vacío en tipo de pago.
5. Se agregó el import `RequestBody`.

### Cómo defenderlo
- **"¿Dónde está la comunicación entre microservicios?"** → En `BoletaService.agregarProducto`: ventas llama por REST (WebClient) al inventario para validar el producto antes de asociarlo.
- **"¿Cómo manejas un error remoto?"** → Con `.onStatus(status -> status.is4xxClientError(), ...)`: si el producto no existe en inventario, lanzo una excepción con mensaje claro en vez de propagar un error técnico.
- **"¿Por qué `@Value` con un valor por defecto?"** → Para que la URL del otro servicio dependa del entorno (local/Docker) sin recompilar.

---

## 3. `ms3` — Bicicleta (REPARADO ✅ compila)

**Dominio:** Bicicletas (con colores y marcas en relación ManyToMany, y un modelo ManyToOne), Marcas, Modelos, Colores.

### Qué estaba roto
- Carpeta `assemblers/` con 4 clases **HATEOAS sin usar**: 3 vacías y `BicicletaAssembler` roto (faltaba la dependencia `spring-hateoas` y los imports estáticos `linkTo`/`methodOn`).
- `ModeloController` no importaba `ModeloService`, `ModeloDTO` ni `Modelo`.
- `BicicletaService` no tenía los métodos `buscarPorCliente/Modelo/Material/Marca` que el controller invoca.
- `MarcaDTO.bicicletas` era `Integer` pero el service le asignaba una **lista** de ids → error de tipos.
- `findByClienteId` requería un campo `clienteId` que el modelo `Bicicleta` no tenía (habría fallado al arrancar).

### Qué se hizo
1. **Se eliminó la carpeta `assemblers/` completa** (código muerto). → cumple el ítem de rúbrica "eliminación de código muerto".
2. Se agregaron los **imports faltantes** en `ModeloController`.
3. Se completó `BicicletaService` con los 4 métodos de búsqueda (delegan en query methods del repositorio) y un `convertirADTO` que ahora llena modelo y marcas.
4. `MarcaDTO.bicicletas` cambió a `List<Integer>` (una marca tiene muchas bicicletas).
5. Se agregó `clienteId` al modelo `Bicicleta` (referencia floja al cliente del ms4).

### Cómo defenderlo
- **"¿Por qué borraste los assemblers?"** → Eran HATEOAS a medio implementar y **ningún controller los usaba**; mantenerlos rompía la compilación y violaba "eliminar código muerto".
- **"¿Qué relaciones tiene Bicicleta?"** → ManyToMany con Color y Marca (tablas intermedias), ManyToOne con Modelo, y una referencia floja `clienteId` al microservicio de personas.
- **"¿Cómo busca por marca si es ManyToMany?"** → query method `findByMarcasId(marcaId)`: Spring Data navega la relación `marcas` → `id`.

---

## 4. `ms4` — Persona (✅ ya compilaba, sin cambios de reparación)

Gestiona Clientes y Empleados. Era el único microservicio sano. (Se le agregarán Swagger, tests y Docker como a los demás.)

---

## 5. `gateway` — API Gateway (REPARADO ✅ compila y enruta)

**Rol:** punto único de entrada (puerto 8080) que enruta a los 4 microservicios.

### Qué estaba roto
- **Typos** `pridcates` (ruta bicicleta) y `pedicates` (ruta persona) en vez de `predicates` → esas dos rutas **no enrutaban**.
- `uri: lb://ms` usa **balanceo de carga con Eureka**, pero no hay servidor Eureka ni los ms son clientes Eureka → no resolvía.
- Sintaxis de predicado inválida: `Path=/api/v1/productos,Path=/api/v1/repuesto` (no se repite `Path=`).

### Qué se hizo
1. Se **quitó la dependencia `eureka-client`** del `pom` (no había discovery que la respaldara).
2. Se reescribió `application.yml`:
   - URIs **directas por HTTP** y **configurables por variable de entorno**: `uri: ${MS_INVENTARIO_URI:http://localhost:8081}` (en local usa localhost; en Docker se sobreescribe con el nombre del contenedor).
   - Predicados `Path` correctos, con varios patrones separados por coma y `/**` para incluir subrutas: `Path=/api/v1/productos/**,/api/v1/repuesto/**,/api/v1/servicios/**`.
   - Corregidos los typos.

### Mapa de rutas (para el README y la defensa)
| Ruta (entra al gateway :8080) | Microservicio destino |
|---|---|
| `/api/v1/productos/**`, `/repuesto/**`, `/servicios/**` | ms inventario (8081) |
| `/api/v1/boletas/**`, `/tipoPago/**` | ms2 ventas (8082) |
| `/api/v1/bicicletas/**`, `/colores/**`, `/marcas/**`, `/modelos/**` | ms3 bicicleta (8083) |
| `/api/v1/clientes/**`, `/empleados/**` | ms4 persona (8084) |

### Cómo defenderlo
- **"¿Qué hace el gateway?"** → Centraliza el acceso: el cliente solo conoce el :8080 y el gateway reenvía según el `Path` al microservicio correcto.
- **"¿Por qué quitaste `lb://` y Eureka?"** → `lb://` necesita un registro de servicios (Eureka) que el proyecto no tiene; con URIs directas configurables por entorno el enrutamiento funciona en local y en Docker sin infraestructura extra.

---

## ✅ Hito 1 completado: los 5 proyectos COMPILAN y el gateway enruta

---

## 6. Bugs adicionales de runtime corregidos

- **`ms3` `Color.java`**: usaba `org.springframework.data.annotation.Id` (de Spring Data) en vez de `jakarta.persistence.Id` (JPA). Compilaba, pero al arrancar JPA no reconocería la clave primaria → "No identifier specified". Corregido al import de JPA.
- **DELETE que siempre devolvía 404**: `ColorController`, `MarcaController` (ms3) y `EmpleadoController` (ms4) comparaban el mensaje con `"Eliminado exitosamente!"`, pero el service devuelve `"...eliminado exitosamente"`. Nunca coincidía → el borrado respondía 404 aunque sí eliminaba. Se cambió la comparación a `.contains("exito")`.
- **Clases vacías eliminadas** (código muerto): `assemblers/` completo en ms3 y `BicicletaValidaciones`.

**Defensa:** "¿Por qué un DELETE devolvía 404 si borraba?" → el controller comparaba con un texto que el service nunca produce; se alineó la comparación.

---

## 7. Documentación Swagger / OpenAPI (los 4 ms)

- Dependencia `springdoc-openapi-starter-webmvc-ui:3.0.0` (compatible con Spring Boot 4) en los 4 `pom.xml`.
- Clase `OpenApiConfig` en cada ms: un `@Bean OpenAPI` con título/descripción/versión del servicio.
- Anotaciones `@Tag` en los controllers principales y `@Operation` + `@ApiResponses` (códigos 200/204/400/404) en `ProductoController` como ejemplo completo.
- **UI:** `http://localhost:<puerto>/swagger-ui.html` · **JSON:** `/v3/api-docs`.

**Defensa:** "¿De dónde salen los ejemplos JSON?" → springdoc los genera automáticamente desde los DTOs/modelos; `@Operation` añade las descripciones legibles.

---

## 8. Pruebas unitarias (JUnit 5 + Mockito + JaCoCo)

- **175 tests**, todos verdes. Cobertura JaCoCo: **ms 83.8% · ms2 88.9% · ms3 86.5% · ms4 85.8%** (todos >80%).
- **Patrón:** `@ExtendWith(MockitoExtension.class)`, `@Mock` del repositorio, `@InjectMocks` del service; estructura **Given–When–Then**.
- Se testean **services** (lógica/reglas de negocio, con mocks) y **controllers** (códigos HTTP).
- La comunicación REST de `agregarProducto` se prueba con un **WebClient simulado** (`Answers.RETURNS_DEEP_STUBS`).
- **H2 en memoria** solo para tests (`src/test/resources/application.properties`) → `mvn test` corre sin MySQL.
- En JaCoCo se excluyen DTOs, modelos y config (POJOs de Lombok que no contienen lógica).

**Defensa:** "¿Por qué mockeas el repositorio?" → para probar la lógica del service de forma aislada, sin depender de la base de datos; el mock simula qué devuelve el repositorio en cada caso.

---

## 9. Configuración YAML con perfiles + Flyway

- Cada `application.properties` se migró a **`application.yml`** con **perfiles `dev` y `prod`**:
  - `dev` (por defecto): base de datos en `localhost`.
  - `prod`: datasource desde **variables de entorno** (`DB_URL`, `DB_USER`, `DB_PASSWORD`) → usado por Docker.
- **Base de datos por microservicio** (database-per-service): `db_inventario`, `db_ventas`, `db_bicicleta`, `db_persona`. Antes todos apuntaban a una sola BD, lo que causaba conflicto en la tabla de historial de Flyway.
- **Flyway**: `db/migration/V1__init_schema.sql` por servicio crea las tablas; `ddl-auto: none` (Hibernate no toca el esquema, lo gobierna Flyway).

**Defensa:** "¿Para qué los perfiles?" → separar la config local de la de despliegue sin tocar el código; en prod los datos sensibles vienen por variables de entorno.

---

## 10. Docker (despliegue local)

- **Dockerfile multi-stage** por servicio (etapa Maven para compilar el jar + etapa JRE liviana).
- **`docker-compose.yml`** en la raíz: MySQL 8 (con healthcheck) + los 4 ms + gateway, en una red común. Los ms usan el perfil `prod` y esperan a que MySQL esté sano (`depends_on: condition: service_healthy`).
- El gateway recibe las URIs de los servicios por variables de entorno (`MS_*_URI=http://ms:8081`, etc.).
- Ejecutar: `docker compose up --build` (requiere Docker Desktop abierto).

> ⚠️ **Pendiente de verificar por ti:** el `docker compose config` valida correcto, pero **no se pudo levantar aquí porque el daemon de Docker no estaba corriendo**. Al ejecutar `docker compose up --build` con Docker Desktop abierto, revisa los logs del primer arranque para confirmar que Flyway aplica las migraciones sin error (es lo único no verificado en runtime).

---

## 11. README.md

Creado en la raíz con: contexto del dominio, integrantes (completar), lista de microservicios, rutas del gateway, enlaces a Swagger, e instrucciones de ejecución local y con Docker.

---

## ✅ Estado final vs. rúbrica

| Ítem rúbrica | Estado |
|---|---|
| Patrón CSR | ✅ |
| Reglas de negocio / validaciones | ✅ |
| Comunicación REST entre ms + manejo de error | ✅ (ms2 → ms) |
| API Gateway con rutas/predicados | ✅ |
| Configuración YAML + perfiles | ✅ |
| Pruebas unitarias Mockito ≥80% | ✅ (84–89%) |
| Swagger/OpenAPI | ✅ |
| Flyway (migraciones) | ✅ (verificar en primer arranque) |
| Docker (despliegue local) | ✅ (requiere daemon para correr) |
| README | ✅ |
| Eliminación de código muerto | ✅ |

### Lo que aún depende de ti (no es código)
- Completar **integrantes** en el README.
- Subir a **GitHub** con commits descriptivos y dar acceso al docente.
- **Tablero Trello** (IE 2.5.2 — organización de tareas).
- **Despliegue remoto** (Railway/Render) si el docente lo exige.
- Verificar el **primer arranque con Docker** (migraciones Flyway).

