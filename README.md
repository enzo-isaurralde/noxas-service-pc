# NOXAS Service PC

Sistema web para un servicio técnico de PC y notebooks **a domicilio** en CABA. El cliente consulta desde la web, recibe una cotización de la visita según su barrio y un comprobante por mail. El técnico gestiona todo desde un panel de administración.

> Proyecto full stack hecho de punta a punta: diseño, backend, frontend, base de datos.

<!-- AGREGAR: captura principal (landing) -->

<img width="1440" height="900" alt="noxas-lading" src="https://github.com/user-attachments/assets/206914dd-4aa9-4ce9-a490-4c602685f97d" />


## Qué hace

- **Cotizador por barrio:** el cliente elige su barrio de CABA y obtiene el costo de la visita al instante. Cada barrio tiene un costo fijo según su zona (anillos por comuna).
- **Validación de horarios de atención:** la consulta solo se acepta dentro de los días y horarios configurados.
- **Validación de cobertura:** si el barrio queda fuera de la zona de servicio, el sistema lo informa con un error claro.
- **Comprobante por consulta:** cada consulta genera un número único con formato `RC-AAAA-NNNN`.
- **Emails automáticos y asíncronos:** confirmación al cliente (con botón para coordinar por WhatsApp) y aviso interno al técnico, sin bloquear la respuesta HTTP.
- **Panel de administración:** login, listado de consultas, detalle de cada una, cambio de estado y acceso directo a WhatsApp con mensaje prearmado.

<!-- AGREGAR: capturas del cotizador, del mail y del panel admin -->


<img width="1440" height="818" alt="Captura de pantalla 2026-11-18 a la(s) 20 08 20" src="https://github.com/user-attachments/assets/86e86056-ed77-4aa7-9e69-78cb5ef1c8d5" />
 
 <img width="1440" height="819" alt="Captura de pantalla 2026-11-18 a la(s) 20 09 03" src="https://github.com/user-attachments/assets/6fa65d0e-5d63-4c59-9e6a-cb5577a1df2a" />


## Zonas de cobertura (ejemplo)

| Zona | Comunas | Costo de visita |
|---|---|---|
| Base | 4 | Gratis |
| Anillo 1 | 1, 3, 5, 7, 8 | $5.000 |
| Anillo 2 | 2, 6, 9, 10, 13 | $7.000 |
| Anillo 3 | 11, 12, 14, 15 | $8.000 |

Los barrios y horarios se cargan con migraciones de Flyway (`V2__seed_barrios.sql`, `V3__seed_horarios.sql`).

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Backend | Spring Boot 3.3.4, Spring Web, Spring Data JPA / Hibernate |
| Seguridad | Spring Security, contraseñas con BCrypt |
| Base de datos | MySQL + Flyway (migraciones versionadas) |
| Vistas | Thymeleaf, Tailwind CSS |
| Mails | Spring Mail con `@Async` |
| Mapeo | MapStruct (entidad ↔ DTO) |
| Build | Maven (wrapper incluido) |

## Arquitectura

Estructura por capas, con DTOs separados de las entidades y un manejador global de excepciones:

```
com.noxas.reparaciones
├── config        # Seguridad, async, mail, usuario admin inicial
├── controller    # api (consultas, cotización) y admin (panel, login)
├── domain        # Entidades y enums
├── dto           # request / response
├── exception     # Excepciones de negocio + GlobalExceptionHandler
├── mapper        # MapStruct
├── repository    # Spring Data JPA
├── security      # UserDetailsService
└── service       # Lógica de negocio (cobertura, horarios, comprobante, emails)
```

## Ejemplo de código

<!-- AGREGAR: un fragmento corto y representativo (por ejemplo, la generación del comprobante o la validación de horarios) -->


<img width="1440" height="900" alt="noxas-comprobante-codigo" src="https://github.com/user-attachments/assets/c59b4434-41d0-4e93-ba19-97ec0c35608a" />


## Cómo correrlo localmente

**Requisitos:** Java 21 y MySQL.

1. Clonar el repo:
   ```bash
   git clone https://github.com/enzo-isaurralde/noxas-service-pc.git
   cd noxas-service-pc
   ```
2. Crear una base de datos vacía en MySQL (el nombre y el usuario están en `application.properties`).
3. Copiar el archivo de ejemplo y completar tus valores:
   ```bash
   cp src/main/resources/application-example.properties src/main/resources/application-local.properties
   ```
   `application-local.properties` está en el `.gitignore`, así que no se sube.
4. Levantar la app (Flyway crea las tablas y carga los datos iniciales):
   ```bash
   ./mvnw spring-boot:run
   ```
5. Abrir `http://localhost:8080`. El panel de administración está en `/admin`.

### Variables de configuración

| Variable | Para qué sirve |
|---|---|
| `DB_PASSWORD` | Contraseña de la base de datos |
| `ADMIN_PASSWORD` | Contraseña del usuario administrador inicial |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | Cuenta SMTP (con Gmail, una clave de aplicación) |
| `MAIL_FROM` | Remitente de los mails |
| `MAIL_NOTIF` | Casilla que recibe los avisos internos |

En producción se definen como variables de entorno del sistema.

## Autor

**Enzo Isaurralde** · Desarrollador Full Stack Java
[Portfolio](https://enzoisaurralde.com) · [LinkedIn](https://linkedin.com/in/enzo-ariel-isaurralde) · [GitHub](https://github.com/enzo-isaurralde)
