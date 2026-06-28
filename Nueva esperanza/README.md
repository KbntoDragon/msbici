# 🚲 Nueva Esperanza — Arquitectura de Microservicios

Sistema distribuido para la gestión de un **taller de bicicletas**, desarrollado con
**Spring Boot 4.1.0 / Java 21**, comunicación REST entre servicios, API Gateway,
documentación Swagger/OpenAPI, pruebas unitarias y despliegue con Docker.

## 👥 Integrantes
- _(Completar: Nombre y Apellido — Nº de equipo)_

## 🧩 Microservicios implementados

| Servicio | Puerto | Dominio | Base de datos |
|---|---|---|---|
| **gateway** | 8080 | API Gateway (Spring Cloud Gateway) | — |
| **ms** (inventario) | 8081 | Productos, Repuestos, Servicios | `db_inventario` |
| **ms2** (ventas) | 8082 | Boletas, Tipos de pago | `db_ventas` |
| **ms3** (bicicleta) | 8083 | Bicicletas, Marcas, Modelos, Colores | `db_bicicleta` |
| **ms4** (persona) | 8084 | Clientes, Empleados | `db_persona` |

## 🔀 Rutas principales del Gateway (entrada única por `:8080`)

| Ruta | Microservicio destino |
|---|---|
| `/api/v1/productos/**`, `/repuesto/**`, `/servicios/**` | ms inventario (8081) |
| `/api/v1/boletas/**`, `/tipoPago/**` | ms2 ventas (8082) |
| `/api/v1/bicicletas/**`, `/colores/**`, `/marcas/**`, `/modelos/**` | ms3 bicicleta (8083) |
| `/api/v1/clientes/**`, `/empleados/**` | ms4 persona (8084) |

> **Comunicación entre servicios:** `ms2` (ventas) consume por REST (WebClient) al `ms`
> (inventario) para validar que un producto existe antes de agregarlo a una boleta
> (`POST /api/v1/boletas/{boletaId}/productos/{productoId}`).

## 📖 Documentación Swagger / OpenAPI

Con cada servicio levantado:

| Servicio | Swagger UI | OpenAPI JSON |
|---|---|---|
| ms inventario | http://localhost:8081/swagger-ui.html | http://localhost:8081/v3/api-docs |
| ms2 ventas | http://localhost:8082/swagger-ui.html | http://localhost:8082/v3/api-docs |
| ms3 bicicleta | http://localhost:8083/swagger-ui.html | http://localhost:8083/v3/api-docs |
| ms4 persona | http://localhost:8084/swagger-ui.html | http://localhost:8084/v3/api-docs |

## ▶️ Ejecución local (sin Docker)

Requisitos: JDK 21, Maven 3.9+, MySQL 8 corriendo en `localhost:3306` (usuario `root`, sin
contraseña). El perfil por defecto es `dev` y crea las bases automáticamente
(`createDatabaseIfNotExist=true`).

```bash
# En 5 terminales (o desde el IDE), una por servicio:
cd ms/ms        && ./mvnw spring-boot:run
cd ms2/ms2      && ./mvnw spring-boot:run
cd ms3/ms3      && ./mvnw spring-boot:run
cd ms4/ms4      && ./mvnw spring-boot:run
cd gateway/gateway && ./mvnw spring-boot:run
```

## 🐳 Ejecución con Docker (recomendado)

Requisito: **Docker Desktop abierto** (daemon corriendo). Levanta MySQL + los 4
microservicios + el gateway, usando el perfil `prod`:

```bash
docker compose up --build
```

- Gateway disponible en http://localhost:8080
- MySQL expuesto en el host en el puerto `3307` (interno `3306`).
- Para detener: `docker compose down` (agregar `-v` para borrar los datos).

## 🧪 Pruebas unitarias y cobertura

Tests con **JUnit 5 + Mockito** (estructura Given–When–Then), usando **H2 en memoria**
(no requieren MySQL). Cobertura medida con **JaCoCo** (>80% en los 4 microservicios).

```bash
cd ms/ms && ./mvnw test          # ejecuta los tests
# Reporte de cobertura: target/site/jacoco/index.html
```

## 🗄️ Migraciones de base de datos

Cada microservicio gestiona su esquema con **Flyway** (`src/main/resources/db/migration/V1__init_schema.sql`).
Las migraciones se aplican automáticamente al arrancar.

## 🛠️ Stack técnico
- Spring Boot 4.1.0, Java 21
- Spring Web (MVC), Spring Data JPA, Spring Validation
- Spring Cloud Gateway (MVC) 2025.1.1
- WebClient (comunicación REST entre servicios)
- MySQL 8 + Flyway
- springdoc-openapi 3.0.0 (Swagger UI)
- JUnit 5, Mockito, JaCoCo
- Docker / Docker Compose
