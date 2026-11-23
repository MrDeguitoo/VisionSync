# Integrante 1 · Backend Spring Boot, base de datos y Swagger

**Proyecto:** VisionSync · EA4 y EA5 (DSY1105)
**Rama sugerida:** `feature/backend-springboot`
**Lee primero:** `00_comun_contrato_y_reglas.md`. El contrato de la API (sección 4) lo defines tú; cualquier cambio se avisa al grupo.

**Indicadores asociados**
- EA4: 3.1.2 Microservicios (15%) y parte de 3.1.3 Integración (15%).
- EA5: 3.1.2 Explicar los microservicios (12%) y 3.1.3 Modificación en vivo (40%).

**Lo que más urgencia tiene:** el resto del equipo depende de que el login y el CRUD de exámenes existan lo antes posible. Entrega primero una versión mínima que funcione, y luego la completas.

---

## 1. Qué debes entregar

- [ ] Proyecto Spring Boot en `backend/` que arranca con un solo comando.
- [ ] Entidades: `Usuario`, `Paciente`, `Examen`, `Documento`.
- [ ] CRUD de pacientes y exámenes, subida y descarga de documentos.
- [ ] Login que devuelve un token y **permisos separados por rol** (TECNOLOGO, MEDICO, PACIENTE).
- [ ] El paciente ve **solo sus propios** exámenes.
- [ ] Base de datos operativa con datos de prueba ficticios.
- [ ] **Swagger UI funcional** con autenticación Bearer, para probar rutas en el navegador.
- [ ] Sección «Backend» en el README: entidades, endpoints, cómo ejecutarlo, URL de Swagger.

---

## 2. Fase 0: crear el proyecto (hoy, para desbloquear a los demás)

1. Genera el proyecto en Spring Initializr (Maven, Java 17, Spring Boot 3.x estable) en la carpeta `backend/` con:
   - Spring Web
   - Spring Data JPA
   - Spring Security
   - Validation
   - H2 Database (archivo local) **o** MySQL/PostgreSQL si prefieren una base «real»
2. Agrega `springdoc-openapi-starter-webmvc-ui` (la serie 2.x es la compatible con Spring Boot 3) y una librería JWT (por ejemplo `jjwt`).
3. Configura `application.properties`:

```properties
server.port=8080
spring.datasource.url=jdbc:h2:file:./data/visionsync;AUTO_SERVER=TRUE
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.servlet.multipart.max-file-size=10MB
app.jwt.secret=${JWT_SECRET:cambiar-este-secreto-de-desarrollo-muy-largo-123456}
app.uploads.dir=./uploads
```

   - Para uso en producción o demo, el secreto real va en una **variable de entorno** (`JWT_SECRET`), no en el repo.
   - `./data` y `./uploads` van al `.gitignore`.
4. Haz commit del esqueleto y abre PR: los demás ya pueden clonar y correrlo.

---

## 3. Modelo de datos (JPA)

```java
@Entity
@Table(name = "examenes")
public class Examen {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @NotBlank private String tipo;                 // Campimetría, Topografía...
    @Enumerated(EnumType.STRING) private Ojo ojo;  // OD, OI, AO
    private String profesional;
    private String sucursal;
    private LocalDateTime fechaHora;
    @Enumerated(EnumType.STRING) private EstadoExamen estado = EstadoExamen.PENDIENTE;
    @Column(length = 1000) private String observaciones = "";
    private LocalDateTime actualizadoEn;

    @PrePersist @PreUpdate
    void touch() { actualizadoEn = LocalDateTime.now(); }

    // getters y setters (generados por el IDE)
}
```

- `Usuario` tiene `email` (único), `passwordHash` (**BCrypt**), `rol` y `pacienteId` opcional (para vincular al rol PACIENTE con su ficha).
- `Documento` guarda `nombreArchivo`, `tipoMime`, `ruta` y `descripcionAccesible` (texto alternativo que la app leerá con TalkBack).
- **Enums:** `Ojo {OD, OI, AO}`, `EstadoExamen {PENDIENTE, REVISADO, AUTORIZADO}`, `Rol {TECNOLOGO, MEDICO, PACIENTE}`.

---

## 4. DTOs y controlador

Usa DTOs (no expongas las entidades directamente). Un `record` simple basta:

```java
public record ExamenDto(
    Long id, Long pacienteId, String pacienteNombre, String pacienteRut,
    @NotBlank String tipo, String ojo, String profesional, String sucursal,
    LocalDateTime fechaHora, String estado, String observaciones,
    Long documentoId, LocalDateTime actualizadoEn) {}
```

```java
@RestController
@RequestMapping("/api/examenes")
@Tag(name = "Exámenes")
public class ExamenController {

    private final ExamenService service;
    public ExamenController(ExamenService service) { this.service = service; }

    @GetMapping
    public List<ExamenDto> listar(@RequestParam(required = false) String texto,
                                  @RequestParam(required = false) String tipo,
                                  @RequestParam(required = false) String estado,
                                  Authentication auth) {
        return service.listar(texto, tipo, estado, auth);   // si es PACIENTE, filtra por su pacienteId
    }

    @PostMapping
    @PreAuthorize("hasRole('TECNOLOGO')")
    public ResponseEntity<ExamenDto> crear(@Valid @RequestBody ExamenDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TECNOLOGO','MEDICO')")
    public ExamenDto actualizar(@PathVariable Long id, @Valid @RequestBody ExamenDto dto, Authentication auth) {
        return service.actualizar(id, dto, auth);   // MEDICO solo cambia estado y observaciones
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TECNOLOGO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
```

- `@PreAuthorize` requiere `@EnableMethodSecurity` en la configuración de seguridad.
- El filtro de «solo sus exámenes» para PACIENTE va **en el servicio**, no en la app: la regla se aplica en el servidor aunque alguien modifique el cliente.
- Maneja errores con `@RestControllerAdvice` para devolver `400` con un mensaje legible cuando falle `@Valid`, `404` cuando no exista el recurso y `403` cuando el rol no corresponda.

---

## 5. Seguridad con JWT (esquema)

1. `POST /api/auth/login` recibe `{email, password}`, valida con `BCryptPasswordEncoder` y devuelve `{token, rol, nombre, pacienteId}`.
2. Un filtro (`OncePerRequestFilter`) lee `Authorization: Bearer ...`, valida el token y carga el usuario con su rol en el `SecurityContext`.
3. `SecurityFilterChain`:
   - Públicos: `/api/auth/login`, `/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**`.
   - Todo lo demás: autenticado.
   - Sesión sin estado (`STATELESS`) y CSRF deshabilitado (es una API con token).
4. El token incluye el email y el rol, con una expiración razonable (por ejemplo, unas horas). Para la demo conviene que dure lo suficiente.

---

## 6. Swagger / OpenAPI

- La documentación se genera sola con springdoc. Quedan disponibles `/swagger-ui.html` (redirige a la interfaz) y `/v3/api-docs`.
- Declara el esquema de seguridad Bearer para poder usar el botón **Authorize** en Swagger:

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info().title("VisionSync API").version("1.0")
                .description("Servicios simulados para exámenes oftalmológicos ficticios"))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components().addSecuritySchemes("bearerAuth",
                new SecurityScheme().type(SecurityScheme.Type.HTTP)
                    .scheme("bearer").bearerFormat("JWT")));
    }
}
```

- Agrega `@Tag`, `@Operation(summary = "...")` y descripciones a los endpoints: la documentación se ve mucho más profesional.
- **Demo en la defensa:** (1) login en Swagger, (2) botón Authorize con el token, (3) ejecutar un `PUT /examenes/{id}` cambiando el estado u observaciones, (4) mostrar el mismo cambio en la app.

---

## 7. Datos de prueba y archivos

- Un `CommandLineRunner` (`DataInitializer`) que, si la base está vacía, crea los 3 usuarios semilla del documento común, 4 a 6 pacientes ficticios y 8 a 12 exámenes de distintos tipos y estados, **vinculando el usuario PACIENTE a un paciente que tenga exámenes**.
- Para los documentos: guarda en `./uploads` un PDF sintético y una imagen de retina de ejemplo (sin datos reales) y crea sus registros `Documento` con `descripcionAccesible`.
- Subida: `POST /api/examenes/{id}/documento` con `MultipartFile`; valida tipo (PDF o imagen) y tamaño; guarda con un nombre generado (no uses el nombre original como ruta, evita *path traversal*).
- Descarga: `GET /api/documentos/{id}/archivo` devuelve el archivo con su `Content-Type`.

---

## 8. Cómo debe poder correr cualquiera

```bash
cd backend
./mvnw spring-boot:run
# Swagger: http://localhost:8080/swagger-ui.html
```

- Para que el teléfono lo alcance, el servidor debe escuchar en la red (por defecto lo hace) y el firewall debe permitir el puerto 8080.
- Documenta en el README los pasos y las variables de entorno (`JWT_SECRET`).
- Prepara un **script de arranque** o instrucciones claras para la defensa: el backend tiene que estar levantado y probado antes de que parta tu turno.

---

## 9. Tareas por orden de prioridad

1. [ ] **Hoy:** proyecto creado, arranca, `.gitignore` con `data/`, `uploads/`, secretos.
2. [ ] Login con JWT y los 3 usuarios semilla (desbloquea al integrante 2).
3. [ ] CRUD de exámenes con filtros y regla de paciente (desbloquea a 2 y 3).
4. [ ] Swagger con Bearer funcionando.
5. [ ] CRUD de pacientes.
6. [ ] Subida y descarga de documentos, con archivos sintéticos.
7. [ ] Manejo de errores y validaciones con mensajes claros.
8. [ ] Documentación en el README y capturas de Swagger en `docs/evidencias/`.
9. [ ] Pruebas del backend (opcionales, pero ayudan a la explicación: al menos una prueba del servicio de exámenes).

---

## 10. Definición de terminado

- [ ] Con un backend recién clonado, `spring-boot:run` levanta el sistema con datos de prueba.
- [ ] En Swagger puedes loguearte y ejecutar un `GET`, un `POST` y un `PUT` por rol.
- [ ] Un PACIENTE no puede ver exámenes ajenos (probado con dos pacientes).
- [ ] Un token inválido o un rol sin permiso devuelve 401 o 403.
- [ ] El teléfono físico alcanza el backend desde la red de prueba.
- [ ] No hay secretos ni bases de datos en el repositorio.

---

## 11. Qué debes poder defender (15 min, sin IA)

- Explicar cada entidad, sus relaciones y por qué elegiste esa base de datos.
- Recorrer los endpoints, qué rol puede usar cada uno y cómo se aplica (filtro JWT, `@PreAuthorize`, regla en el servicio).
- Abrir Swagger, autenticarte y ejecutar una petición; mostrar el mismo dato actualizado en la app.
- **Cambio en vivo (40%):** agregar el campo (por ejemplo `observacionBreve`) en la entidad, el DTO y el mapeo; reiniciar; probar en Swagger; y que se vea en la app. Practícalo hasta hacerlo en menos de 5 minutos, y ayuda a los demás a practicar la parte que a ti te toca de la cadena.
- Explicar por qué las contraseñas se guardan hasheadas y por qué el filtro de pacientes va en el servidor.

### Ejercicio de práctica sugerido

Agrega los campos `lateralidad` y `observacionBreve` a `Examen`, cronometrado. Si algo tarda, anota qué archivo no recordabas y repasa esa parte.
