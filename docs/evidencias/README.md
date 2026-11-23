# Evidencias de Accesibilidad - VisionSync

Este directorio contiene las capturas de pantalla y evidencias de las pruebas de accesibilidad realizadas con **TalkBack** y **tamaño de fuente máximo**.

## Capturas requeridas para la defensa

### 1. Login
- [ ] Pantalla de login con TalkBack activo
- [ ] Error de validación anunciado por TalkBack (campo vacío, credenciales inválidas)
- [ ] Tamaño de fuente máximo - sin corte de contenido

### 2. Dashboards por rol
- [ ] Dashboard Tecnólogo - navegación por elementos
- [ ] Dashboard Médico (Buscador) - filtros, lista de exámenes
- [ ] Dashboard Paciente - lista de sus exámenes

### 3. Registro de Paciente (Tecnólogo)
- [ ] Formulario de registro con etiquetas accesibles
- [ ] Error de validación RUT anunciado
- [ ] Error de validación nombre anunciado
- [ ] Tamaño de fuente máximo - scroll funcional

### 4. Registro de Examen (Tecnólogo)
- [ ] Formulario completo con nuevos campos (ojo, profesional, sucursal, fecha/hora)
- [ ] Selectores (ExposedDropdownMenu) navegables con TalkBack
- [ ] Errores de validación anunciados
- [ ] Selector de archivo (SAF) accesible
- [ ] Tamaño de fuente máximo - scroll funcional

### 5. Historial y Búsqueda (Médico)
- [ ] Lista de exámenes con mergeDescendants - se lee tarjeta completa
- [ ] Filtros por tipo accesibles
- [ ] Búsqueda por nombre/RUT anunciada
- [ ] Estado vacío anunciado

### 6. Detalle de Examen (Médico)
- [ ] Todos los datos leídos en orden lógico
- [ ] Observaciones y estado anunciados
- [ ] Botones de acción (Autorizar, Ver PDF, Ver Imagen) accesibles

### 7. Resumen de Examen (Paciente)
- [ ] Solo lectura - datos anunciados correctamente
- [ ] Estado del examen anunciado

### 8. Visor PDF
- [ ] PDF renderizado correctamente
- [ ] Descripción accesible (descripcionAccesible) leída por TalkBack
- [ ] Navegación entre páginas
- [ ] Botón cerrar accesible

### 9. Visor de Imagen con Zoom
- [ ] Imagen cargada con Coil
- [ ] Descripción accesible leída
- [ ] Botones de zoom (+/-) ≥ 48dp con contentDescription
- [ ] Gestos de zoom funcionan
- [ ] Botón cerrar accesible

### 10. Pacientes de Prueba (API Externa) - Médico
- [ ] Lista de pacientes simulados
- [ ] Cada tarjeta con mergeDescendants - se lee completa
- [ ] Estado de carga anunciado
- [ ] Error de red anunciado
- [ ] Botón actualizar accesible

### 11. Banner de Estado de Conexión
- [ ] Estado "Sincronizado" anunciado al cambiar
- [ ] Estado "Sin conexión" anunciado al cambiar
- [ ] liveRegion Polite funciona sin interrumpir

### 12. Tamaño de Fuente Máximo (Global)
- [ ] Todas las pantallas desplazables (verticalScroll / LazyColumn)
- [ ] No hay contenido cortado
- [ ] Controles táctiles ≥ 48dp mantenidos
- [ ] Textos en `sp` escalan correctamente

---

## Cómo tomar las capturas

1. **Activar TalkBack**: Ajustes → Accesibilidad → TalkBack → Activar
2. **Tamaño de fuente máximo**: Ajustes → Accesibilidad → Tamaño de fuente → Máximo
3. **Tamaño de pantalla grande** (opcional): Ajustes → Pantalla → Tamaño de pantalla → Grande
4. Navegar por cada flujo completo
5. Tomar captura de pantalla en cada estado relevante
6. Guardar en este directorio con nombres descriptivos:
   - `01_login_talkback.png`
   - `02_dash_tecnologo_talkback.png`
   - `03_registro_paciente_error.png`
   - etc.

## Verificación de Contraste (WCAG 2.2 AA)

- [ ] Texto normal: contraste ≥ 4.5:1
- [ ] Texto grande (≥18sp o ≥14sp bold): contraste ≥ 3:1
- [ ] Componentes UI (bordes, iconos): contraste ≥ 3:1
- [ ] Verificado con Accessibility Scanner de Android

## Controles Táctiles (≥ 48dp)

- [ ] Botones principales
- [ ] Iconos clicables
- [ ] Chips de filtro
- [ ] Botones de zoom en visor de imagen
- [ ] Filas de lista (examenes, pacientes)

---

*Generado como parte de la implementación de accesibilidad para VisionSync - EA4/EA5*