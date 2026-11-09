# Telecomunicaciones-Backend

API REST (microservicios Spring Boot) para una empresa de telecomunicaciones: catálogo de planes,
clientes, contrataciones, instalaciones, facturación mensual, pagos y avisos por falta de pago.
El frontend vive en otro repositorio y consume todo a través del API Gateway.

## Arquitectura

| Módulo | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| `discovery-server` | 8761 | — | Eureka: los servicios se encuentran por nombre |
| `config-server` | 8888 | — | Configuración centralizada (`config-server/src/main/resources/config/`) |
| `api-gateway` | 8080 | — | Única entrada para el frontend |
| `usuarios-service` | 8081 | `usuarios` | Login/registro, personal, clientes, técnicos |
| `catalogo-service` | 8082 | `catalogo` | Planes (catálogo público) |
| `contrataciones-service` | 8083 | `contrataciones` | Contrataciones, instalaciones, bajas y reactivaciones |
| `facturacion-service` | 8084 | `facturacion` | Facturas mensuales, pagos, débito automático, vencimientos y avisos |
| `seguridad-comun` | — | — | Librería: JWT de la cookie y token entre servicios |
| `errores-comun` | — | — | Librería: errores traducidos (español / inglés) |

## Requisitos
- Java 25 (Maven no hace falta: se usa `./mvnw`)
- PostgreSQL con un usuario dueño de las bases `usuarios`, `catalogo`, `contrataciones` y `facturacion`

```sql
CREATE ROLE telecom LOGIN PASSWORD 'telecom_dev';
CREATE DATABASE usuarios OWNER telecom;
CREATE DATABASE catalogo OWNER telecom;
CREATE DATABASE contrataciones OWNER telecom;
CREATE DATABASE facturacion OWNER telecom;
```

## Variables de entorno

| Variable | Obligatoria | Uso |
|---|---|---|
| `JWT_SECRETO` | Sí | Firma de los tokens (32+ caracteres, el mismo en todos los servicios) |
| `ADMIN_EMAIL`, `ADMIN_CONTRASENA` | Sí (usuarios) | Primer administrador, se crea al arrancar |
| `DB_USUARIO`, `DB_CONTRASENA` | No (`postgres`/`postgres`) | Credenciales de PostgreSQL |
| `COOKIE_SEGURA` | No (`true`) | `false` en local (http) |
| `SPRING_MAIL_HOST` (+ `_PORT`, `_USERNAME`, `_PASSWORD`) | No | Envío real de emails; sin esto se escriben en el log |
| `EMAIL_REMITENTE` | No | Remitente de los emails |
| `EUREKA_URL`, `CONFIG_SERVER_URL`, `DB_URL` | No | Para otros entornos |

## Cómo levantarlo
En este orden, cada uno en su terminal (con las variables de entorno definidas):

```bash
./mvnw -DskipTests install
./mvnw -pl discovery-server spring-boot:run
./mvnw -pl config-server spring-boot:run
./mvnw -pl usuarios-service spring-boot:run
./mvnw -pl catalogo-service spring-boot:run
./mvnw -pl contrataciones-service spring-boot:run
./mvnw -pl facturacion-service spring-boot:run
./mvnw -pl api-gateway spring-boot:run
```

Tests: `./mvnw test` (usan H2; no hace falta PostgreSQL ni levantar nada).

## Endpoints principales (a través de `http://localhost:8080`)

| Rol | Endpoints |
|---|---|
| Público | `POST /api/auth/login`, `POST /api/auth/registro`, `POST /api/auth/logout`, `GET /api/planes`, `GET /api/planes/{id}` |
| Cualquier rol con sesión | `GET /api/usuarios/yo` (sus propios datos) |
| Administrador | `POST/PUT/DELETE /api/planes`, `GET/POST /api/usuarios`, `PUT /api/usuarios/{id}/rol`, `PUT /api/usuarios/{id}/desactivar`, `GET /api/usuarios/reporte`, `GET /api/contrataciones/reporte`, `GET /api/facturas/reporte` |
| Empleado | `/api/clientes` (alta, búsqueda, edición), `GET /api/tecnicos`, `POST/GET /api/contrataciones/clientes/{clienteId}`, `PUT /api/contrataciones/{id}/baja`, `GET /api/instalaciones/pendientes`, `PUT /api/instalaciones/{id}/tecnico`, `GET /api/contrataciones/suspendidas`, `PUT /api/contrataciones/{id}/reactivacion` |
| Técnico | `GET /api/instalaciones/mias`, `PUT /api/instalaciones/{id}/realizada` |
| Cliente | `POST /api/contrataciones`, `GET /api/contrataciones/mias`, `GET /api/facturas/mias`, `POST /api/facturas/{id}/pagos`, `GET/PUT/DELETE /api/facturas/debito-automatico`, `GET /api/notificaciones/mias`, `PUT /api/notificaciones/{id}/leida` |

Los errores devuelven `ProblemDetail` en el idioma de `Accept-Language` (`es` por defecto, `en`).

## Reportes (dashboard del administrador)
Los tres reportes reciben `?desde=AAAA-MM-DD&hasta=AAAA-MM-DD&agrupacion=DIA|MES` (por defecto `DIA`).
Un día, un mes o un año es el mismo pedido con otro rango. Cada uno trae totales del período y una
`serie` sin huecos (un punto por día o por mes) para los gráficos.

| Reporte | Qué trae |
|---|---|
| `GET /api/usuarios/reporte` | Clientes nuevos, clientes activos y personal activo por rol |
| `GET /api/contrataciones/reporte` | Contrataciones nuevas, bajas, suspendidas, reactivadas, instalaciones (realizadas y pendientes), por plan y por técnico |
| `GET /api/facturas/reporte` | Facturado, cobrado, deuda actual, facturas por estado, cobrado por medio de pago y débitos automáticos |

## Procesos automáticos
`facturacion-service` corre todos los días a las 2 AM (`facturacion.cron`):
1. Genera las facturas del mes (la primera llega un mes después de contratar; vence a los 10 días).
2. Cobra con débito automático las facturas adeudadas de quien lo tenga activo.
3. Marca las vencidas y avisa al cliente; a los 14 días de vencida sin pagar, suspende el plan.

## Pagos
Por ahora se usa `PasarelaSimulada` (no cobra de verdad: aprueba salvo el token `rechazado`).
Para conectar Mercado Pago, PayPal o un procesador de tarjetas hay que implementar `PasarelaDePago`.
El frontend tokeniza la tarjeta con el SDK del proveedor: los datos de la tarjeta nunca llegan al backend.
