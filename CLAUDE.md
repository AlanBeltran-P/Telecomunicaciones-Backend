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
- PostgreSQL (H2 para tests)
- Solo API REST; el frontend vive en otro repo
- Sesión: JWT en cookie HttpOnly, a través del Gateway
- Pagos: tarjeta de débito/crédito, Mercado Pago y PayPal (manual y débito automático)
- Notificaciones: dentro del sistema y por email

## Convenciones
- Mapeo entre entidades y DTOs con MapStruct (en todos los microservicios)
- Cada feature se organiza en subcarpetas controller, service, repository, entity, dto, mapper, exception y client (llamadas a otros microservicios); solo las que use, sin carpetas vacías
- Cada microservicio se trabaja en su propia rama y PR
- Los PRs los mergea Claude con merge commit, sin borrar la rama

## Decisiones caras
- Una sola empresa (no multi-tenant)
- Roles fijos: administrador, empleado, técnico, cliente
- Idiomas: inglés y español
- Moneda: solo pesos
- Sin modo offline
