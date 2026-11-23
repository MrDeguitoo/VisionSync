# VisionSync Backend

Backend Spring Boot para la plataforma VisionSync - Gestión Centralizada y Trazabilidad de Exámenes Oftalmológicos.

## Tecnologías

- **Spring Boot 3.2.5**
- **Java 17**
- **Spring Data JPA** + **H2 Database** (archivo local)
- **Spring Security** + **JWT** (jjwt 0.12.5)
- **Springdoc OpenAPI** (Swagger UI)
- **Maven**

## Ejecución

```bash
cd backend
./mvnw spring-boot:run
```

- **Servidor**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:file:./data/visionsync`)

## Usuarios de prueba (semilla)

| Email | Contraseña | Rol | Paciente asociado |
|-------|------------|-----|-------------------|
| `tecnologo@visionsync.test` | `demo1234` | TECNOLOGO | - |
| `medico@visionsync.test` | `demo1234` | MEDICO | - |
| `paciente@visionsync.test` | `demo1234` | PACIENTE | Paciente Ficticio Uno (11111111-1) |

## Endpoints principales

### Autenticación
- `POST /api/auth/login` - Login, devuelve `{token, rol, nombre, pacienteId}`

### Pacientes (TECNOLOGO, MEDICO; PACIENTE solo el suyo)
- `GET /api/pacientes` - Listar
- `GET /api/pacientes/{id}` - Detalle
- `POST /api/pacientes` - Crear (TECNOLOGO)
- `PUT /api/pacientes/{id}` - Actualizar (TECNOLOGO)
- `DELETE /api/pacientes/{id}` - Eliminar (TECNOLOGO)

### Exámenes (TECNOLOGO, MEDICO; PACIENTE solo los suyos)
- `GET /api/examenes?texto=&tipo=&estado=` - Listar con filtros
- `GET /api/examenes/{id}` - Detalle
- `POST /api/examenes` - Crear (TECNOLOGO)
- `PUT /api/examenes/{id}` - Actualizar (TECNOLOGO todo; MEDICO estado + observaciones)
- `DELETE /api/examenes/{id}` - Eliminar (TECNOLOGO)
- `POST /api/examenes/{id}/documento` - Subir PDF/imagen (TECNOLOGO)
- `GET /api/documentos/{id}/archivo` - Descargar archivo

## Valores permitidos

- `tipo`: Campimetría, Topografía, Fondo de ojo, Agudeza visual, Campimetría Computarizada, Tomografía de Coherencia Óptica (OCT), Tonometría y Curva de Tensión
- `ojo`: OD, OI, AO
- `estado`: PENDIENTE, REVISADO, AUTORIZADO

## Configuración

Variables de entorno:
- `JWT_SECRET` - Clave secreta para firmar tokens (mín. 256 bits, en Base64)

Archivo `application.properties`:
```properties
server.port=8080
spring.datasource.url=jdbc:h2:file:./data/visionsync;AUTO_SERVER=TRUE
app.jwt.secret=${JWT_SECRET:cambiar-este-secreto-de-desarrollo-muy-largo-123456}
app.jwt.expiration=86400000
app.uploads.dir=./uploads
```

## Red (para probar con teléfono físico)

| Escenario | URL del backend |
|-----------|-----------------|
| Emulador Android | `http://10.0.2.2:8080/api/` |
| Teléfono físico (misma Wi-Fi) | `http://<IP-LAN-del-PC>:8080/api/` |

> **Nota**: Android bloquea HTTP sin cifrar. Para demo se permite con `android:usesCleartextTraffic="true"` en el manifest de la app. Solo para demostración académica.

## Swagger en la defensa

1. Abrir http://localhost:8080/swagger-ui.html
2. `POST /api/auth/login` con `tecnologo@visionsync.test` / `demo1234`
3. Copiar token → botón **Authorize** → pegar `Bearer <token>`
4. Ejecutar `PUT /api/examenes/{id}` cambiando estado a `AUTORIZADO`
5. Ver el mismo cambio reflejado en la app Android

## Cambio en vivo (40% defensa)

Ejemplo: agregar campo `observacionBreve` a `Examen`:
1. Entidad `Examen.java`: `@Column(length = 200) private String observacionBreve;`
2. DTO `ExamenDto.Request/Response`: agregar campo
3. Service `ExamenService`: mapear en `toResponse` y `actualizar`
4. Reiniciar backend → probar en Swagger → ver en app

## Estructura

```
backend/
├── src/main/java/com/duoc/visionsync/
│   ├── VisionSyncBackendApplication.java
│   ├── config/OpenApiConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── PacienteController.java
│   │   └── ExamenController.java
│   ├── model/
│   │   ├── entity/ (Usuario, Paciente, Examen, Documento)
│   │   ├── dto/ (AuthDtos, ExamenDto, PacienteDto)
│   │   └── enum/ (Rol, Ojo, EstadoExamen)
│   ├── repository/ (JpaRepository interfaces)
│   ├── security/ (JwtUtil, JwtAuthenticationFilter, SecurityConfig)
│   ├── service/ (AuthService, PacienteService, ExamenService, DocumentoService)
│   └── util/DataInitializer.java (CommandLineRunner con datos semilla)
├── src/main/resources/application.properties
├── pom.xml
└── .gitignore
```

## Pruebas

```bash
./mvnw test
```

## Nota sobre datos

Todos los datos son **ficticios** (MVP académico). No se usan datos clínicos reales. Contraseñas hasheadas con BCrypt.