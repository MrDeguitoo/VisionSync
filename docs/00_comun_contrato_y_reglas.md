# VisionSync · EA4 (encargo) y EA5 (defensa) · Documento común

Léanlo los 4 antes de empezar. Cada integrante tiene además su propio archivo (`integrante_1…4`).

- **EA4 (encargo, 6%)**: se entrega en AVA en la semana 15. Código Android + backend, APK release firmada, README técnico, evidencias (Swagger, pruebas y cobertura) y enlace del GitHub público.
- **EA5 (defensa individual, 24%)**: semana 16, 15 minutos por persona, APK instalada en teléfono físico y proyectada. Orden al azar: **todos deben estar listos desde el primer día**.
- **Importante**: si se detectan cambios en el repositorio después de la fecha límite, la pauta asigna 1.0. Cada integrante debe además activar su registro individual en AVA.

> Las pautas hablan de «parejas» y de «aportes de ambos integrantes». Somos 4: **confirmen con el docente** cómo se evalúa y cómo se entrega, antes de cerrar el reparto.

---

## 1. Qué pesa en la nota

| EA4 (entrega) | Peso | | EA5 (defensa individual) | Peso |
|---|---|---|---|---|
| Interfaz y flujos (3.1.1) | 15% | | Funcionamiento completo (3.1.1) | 12% |
| Microservicios (3.1.2) | 15% | | Explicar microservicios (3.1.2) | 12% |
| Integración móvil ↔ backend (3.1.3) | 15% | | **Modificación en vivo (3.1.3)** | **40%** |
| API externa (3.1.4) | 15% | | Ejecutar una prueba (3.2.1) | 12% |
| Pruebas unitarias >90% (3.2.1) | 15% | | Explicar el rol de las pruebas (3.2.2) | 8% |
| APK firmada (3.3.1) | 10% | | Explicar firma de APK (3.3.1) | 8% |
| GitHub + Trello (3.3.2) | 15% | | Mostrar aportes GitHub/Trello (3.3.2) | 8% |

**Regla de oro:** el 40% de la defensa es agregar un campo de punta a punta (entidad, endpoint, persistencia, Retrofit, ViewModel, pantalla) en menos de 5 minutos. **Los 4 deben poder hacerlo solos.** Ver el ejercicio de práctica en la sección 10.

Además: si la app no ejecuta bien en la defensa, **los ítems siguientes valen cero**. Prioricen que el flujo principal funcione sobre agregar extras.

---

## 2. Reparto de roles

| # | Rol | Entrega principal |
|---|---|---|
| **Diego** | Backend Spring Boot | API, base de datos, seguridad por rol, Swagger, datos de prueba |
| **Job** | Integración móvil | Retrofit, Repository, Room offline y sincronización, token cifrado, API externa (HAPI FHIR) |
| **Seba** | Interfaz y accesibilidad | Pantallas, TalkBack, texto ampliado, visor PDF, imágenes con zoom |
| **Isi** | Calidad, firma y gestión | Pruebas y cobertura >90%, APK firmada, GitHub, Trello, README, evidencias |

Nadie trabaja solo en su rol: el integrante 4 también ayuda a probar el backend, el 3 se coordina con el 2 para los estados de carga y error, etc.

---

## 3. Arquitectura

```
Compose UI  →  ViewModel  →  Repository  ─┬→  Retrofit  →  Spring Boot  →  Base de datos remota
                                          ├→  Retrofit  →  HAPI FHIR (API externa, solo lectura)
                                          └→  Room (fuente local, modo offline)
```

- La **UI** nunca llama a Retrofit ni a Room directamente.
- El **Repository** es *offline-first*: la UI observa Room; el Repository refresca desde la API y envía lo pendiente.
- El **token de sesión** se guarda cifrado con Android Keystore.

### Estructura del repositorio (monorepo)

```
VisionSync/
├── app/                 (Android, ya existe)
├── backend/             (Spring Boot, NUEVO)
├── docs/                (diseño y evidencias/)
├── .gitignore           (agregar *.jks, keystore.properties, local.properties, application-local.properties)
└── README.md
```

---

## 4. Contrato de la API (lo define el integrante 1; cualquier cambio se avisa al grupo)

Base: `http://<host>:8080/api` · Autenticación: `Authorization: Bearer <token>`.

| Método | Ruta | Roles | Descripción |
|---|---|---|---|
| POST | `/auth/login` | público | Devuelve `{token, rol, nombre, pacienteId}` |
| GET | `/pacientes` | TECNOLOGO, MEDICO | Lista de pacientes |
| GET | `/pacientes/{id}` | TECNOLOGO, MEDICO, PACIENTE (solo el propio) | Detalle |
| POST | `/pacientes` | TECNOLOGO | Alta |
| PUT | `/pacientes/{id}` | TECNOLOGO | Actualización |
| GET | `/examenes?texto=&tipo=&estado=` | TECNOLOGO, MEDICO; PACIENTE ve **solo los suyos** | Listado con búsqueda y filtros |
| GET | `/examenes/{id}` | según rol | Detalle |
| POST | `/examenes` | TECNOLOGO | Alta |
| PUT | `/examenes/{id}` | TECNOLOGO (datos), MEDICO (estado y observaciones) | Actualización |
| DELETE | `/examenes/{id}` | TECNOLOGO | Baja |
| POST | `/examenes/{id}/documento` | TECNOLOGO | Sube PDF o imagen (`multipart/form-data`) |
| GET | `/documentos/{id}/archivo` | según rol | Descarga el archivo |

Códigos: `200/201` ok, `400` validación (con mensaje), `401` sin token, `403` sin permiso, `404` no existe.

### Formato de un examen (JSON)

```json
{
  "id": 12,
  "pacienteId": 3,
  "pacienteNombre": "Paciente Ficticio Uno",
  "pacienteRut": "11111111-1",
  "tipo": "Campimetría",
  "ojo": "OD",
  "profesional": "Dra. Ficticia",
  "sucursal": "Sucursal Centro",
  "fechaHora": "2026-11-20T10:30:00",
  "estado": "PENDIENTE",
  "observaciones": "",
  "documentoId": 7,
  "actualizadoEn": "2026-11-20T10:31:00"
}
```

Valores permitidos: `ojo` = `OD`, `OI`, `AO` · `estado` = `PENDIENTE`, `REVISADO`, `AUTORIZADO` · `tipo` = Campimetría, Topografía, Fondo de ojo, Agudeza visual.

---

## 5. Modelo de datos

**Backend (base remota):** `Usuario(id, email, passwordHash, rol, pacienteId?)`, `Paciente(id, rut, nombre, fechaNacimiento)`, `Examen(id, paciente, tipo, ojo, profesional, sucursal, fechaHora, estado, observaciones, actualizadoEn)`, `Documento(id, examen, nombreArchivo, tipoMime, ruta, descripcionAccesible)`.

**Android (Room):** la misma información más campos de sincronización:

| Campo extra en `ExamenEntity` | Para qué |
|---|---|
| `remoteId: Long?` | id del servidor (null si aún no se envió) |
| `syncState: String` | `SINCRONIZADO`, `PENDIENTE` o `ERROR` |
| `documentoLocalPath: String?` | archivo ya descargado, para verlo sin conexión |
| `actualizadoEn: String` | resolución simple: gana el dato más reciente |

Estados visibles en la app: «Modo offline», «Sincronización pendiente (N)», «Sincronizado», «Error de sincronización».

> Al subir la versión de la base de Room (por ejemplo al agregar un campo), hoy se usa `fallbackToDestructiveMigration()`, que borra los datos locales. Es aceptable porque se vuelven a bajar del servidor, pero **no cambien la versión justo antes de una demo offline**.

---

## 6. Datos de prueba (todo ficticio)

- Usuarios semilla del backend: `tecnologo@visionsync.test`, `medico@visionsync.test`, `paciente@visionsync.test` con una contraseña de demo (`demo1234`). En el backend se guardan **hasheadas (BCrypt)**.
- Pacientes, exámenes y observaciones inventados. PDF e imágenes **sintéticos** (por ejemplo, un PDF generado y una imagen de retina de ejemplo sin datos reales).
- Prohibido subir datos clínicos reales, contraseñas reales, claves o `.jks` al repositorio.
- La app es un MVP académico: **no interpreta diagnósticos**.

---

## 7. Red y configuración (ojo: es donde más se falla en una defensa)

| Escenario | URL del backend |
|---|---|
| Emulador | `http://10.0.2.2:8080/api/` |
| Teléfono físico (misma red Wi-Fi que el PC) | `http://<IP-LAN-del-PC>:8080/api/` |

- La URL va en `local.properties` (no se sube) y llega a la app por `BuildConfig`. Se documenta un `local.properties.example`.
- Android bloquea HTTP sin cifrar. Para la demo se permite con `android:usesCleartextTraffic="true"` o con un `network_security_config`. Documenten en el README que es solo para demostración.
- Redes de laboratorio a veces **aíslan los equipos entre sí**: tengan un plan B (hotspot del teléfono del PC o despliegue del backend en línea). Probar esto con el teléfono físico **antes** de la semana de defensa.
- En Windows, permitir el puerto 8080 en el firewall.

---

## 8. Reglas de Git

- Ramas: `feature/backend-...`, `feature/movil-...`, `feature/ui-...`, `feature/calidad-...`. Todo entra por Pull Request revisado por otra persona.
- Commits con prefijo: `feat:`, `fix:`, `test:`, `docs:`, `chore:`. Mensajes descriptivos y cuenta propia de cada uno.
- Ningún secreto en el repo. Verifiquen el `.gitignore` antes del primer commit del backend y de la firma.
- **Después de la fecha límite de entrega no se toca el repositorio.**

---

## 9. Fases e hitos de integración

| Fase | Qué se hace | Hito de salida |
|---|---|---|
| **0. Contrato** | Acordar este documento, crear `backend/`, actualizar `.gitignore`, crear el tablero Trello de EA4/EA5 | Contrato aprobado por los 4 |
| **1. Núcleo** | 1: login y CRUD de exámenes. 2: Retrofit + token, contra un servidor simulado mientras tanto. 3: pantallas con datos del Repository. 4: Kover, MockK, firma de prueba | Login por rol contra el backend real |
| **2. Integración** | Offline y sincronización, visor PDF e imagen, API externa, accesibilidad | Recorrido completo online y offline |
| **3. Endurecimiento** | Cobertura >90%, Compose UI Test, APK release final, README y evidencias | APK instalada en teléfono físico funcionando |
| **4. Ensayo** | Cada uno ensaya los 15 minutos y el cambio en vivo | Ensayo cronometrado sin ayuda |

---

## 10. Ejercicio de práctica: el cambio en vivo (40% de la defensa)

Ejemplo pedido por la pauta: agregar el campo ficticio **«observación breve»** o **«lateralidad»** a un examen. La cadena completa es:

1. **Backend:** campo en la entidad `Examen`, en el DTO y en el mapeo del controlador/servicio. Reiniciar y probar en Swagger.
2. **Móvil, red:** campo en el DTO de Retrofit.
3. **Móvil, local:** campo en `ExamenEntity` de Room (subir la versión) y en el mapeo de entidad a modelo.
4. **Repository / ViewModel:** pasar el campo en el estado de la pantalla y en la validación si corresponde.
5. **Compose:** mostrar o editar el campo en la pantalla.
6. **Verificar:** crear o editar desde el teléfono y ver el dato en Swagger (y al revés).

**Cómo practicarlo:** cada integrante, por turnos y **cronometrado, sin ayuda ni IA**, agrega un campo distinto. Objetivo: menos de 5 minutos. Si alguien no sabe en qué archivo tocar, ese es su punto débil: repasen la cadena juntos.

---

## 11. Secuencia de la defensa (15 min, sugerida por el docente)

| Min | Qué se muestra |
|---|---|
| 0-2 | APK en teléfono físico, login por rol, arquitectura |
| 2-5 | Paciente, examen, historial, PDF e imagen, API externa |
| 5-7 | Spring Boot, Swagger, endpoint ejecutado y dato reflejado en la app |
| 7-9 | Desconectar el servidor: Room, «Modo offline», sincronización pendiente; reconectar |
| 9-10 | TalkBack y texto ampliado |
| 10-11 | Pruebas, MockK, Compose UI Test, cobertura |
| 11-15 | Firma, GitHub y Trello, cambio en vivo (Paso 9) |

---

## 12. Checklist de entrega en AVA (semana 15)

- [ ] Código fuente Android y backend (un integrante lo adjunta).
- [ ] APK release firmada e instalable, con `versionCode` y `versionName`.
- [ ] README técnico completo y evidencias (capturas, Swagger, cobertura, sincronización, versión de APK).
- [ ] Enlace al GitHub **público**, enviado también antes de la defensa.
- [ ] Cada integrante activó su registro individual en AVA.
- [ ] Sin `.jks`, contraseñas ni secretos en el repositorio.
- [ ] Repositorio congelado.
