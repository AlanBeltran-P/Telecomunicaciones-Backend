# Telecomunicaciones-Backend

## Contexto
API REST para una empresa de telecomunicaciones. Los clientes ven el catálogo,
contratan planes y consultan y pagan sus facturas. Los empleados gestionan
clientes, asignan instalaciones y resuelven bajas por falta de pago. Los
técnicos hacen las instalaciones y el administrador gestiona usuarios y el
catálogo.

## Stack
- Spring Boot (Java 25), microservicios con Maven multi-módulo (Maven Wrapper)
- API Gateway + Eureka (discovery) + Config Server (configuración centralizada, perfil native)
- Orden de arranque: discovery-server → config-server → resto de servicios
- PostgreSQL (H2 para tests), una base por microservicio
- Solo API REST; el frontend vive en otro repo
- Sesión: JWT en cookie HttpOnly, a través del Gateway
- Pagos: tarjeta de débito/crédito, Mercado Pago y PayPal (manual y débito automático)
- Notificaciones: dentro del sistema y por email

## Módulos
- `discovery-server` (8761), `config-server` (8888), `api-gateway` (8080)
- `usuarios-service` (8081): login, personal, clientes, técnicos
- `catalogo-service` (8082): planes
- `contrataciones-service` (8083): contrataciones, instalaciones, suspensión/reactivación
- `facturacion-service` (8084): facturas, pagos, débito automático, vencimientos, notificaciones
- `seguridad-comun`: validación del JWT de la cookie y token de servicio (librería)
- `errores-comun`: `ErrorDeNegocio` y traducción de errores español/inglés (librería)

## Convenciones
- Mapeo entre entidades y DTOs con MapStruct (en todos los microservicios)
- Cada feature se organiza en subcarpetas controller, service, repository, entity, dto, mapper, exception y client (llamadas a otros microservicios); solo las que use, sin carpetas vacías
- Cada microservicio se trabaja en su propia rama y PR
- Los PRs los mergea Claude con merge commit, sin borrar la rama
- Errores para el usuario: extender `ErrorDeNegocio` (código HTTP + clave), con el texto en `messages.properties` (español) y `messages_en.properties` (inglés). Nada de `@ExceptionHandler` por servicio
- Llamadas entre servicios: por Eureka con el `RestClient.Builder` `@LoadBalanced` (clonarlo en cada cliente); sin usuario de por medio, con `TokenDeServicio` (rol `SISTEMA`)
- Endpoints solo para otros servicios: bajo `/api/internos/**` con `hasRole(SISTEMA)`; el Gateway no los enruta
- El usuario se identifica por `SeguridadJwt.usuarioId(jwt)` (id que no cambia), nunca por el email
- Los cambios de estado no borran datos (baja lógica: `BAJA`, `SUSPENDIDA_POR_FALTA_DE_PAGO`, `activo`)

## Decisiones caras
- Una sola empresa (no multi-tenant)
- Roles fijos: administrador, empleado, técnico, cliente
- Idiomas: inglés y español
- Moneda: solo pesos
- Sin modo offline

## Pendientes conocidos
- Integraciones reales de pago (hoy `PasarelaSimulada`): Mercado Pago, PayPal y procesador de tarjetas
- Flyway para migraciones (con `ddl-auto: update` hubo que tocar la base a mano: columnas y `CHECK` de enums)
- Idioma preferido del cliente para los avisos automáticos (hoy salen en español)
- Revocar tokens al desactivar un usuario (hoy vale hasta que vence, 8 h)
