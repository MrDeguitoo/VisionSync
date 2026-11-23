# Integrante Seba · Interfaz, accesibilidad visual y visores (PDF e imágenes)

**Proyecto:** VisionSync · EA4 y EA5 (DSY1105)
**Rama sugerida:** `feature/ui-accesibilidad`
**Lee primero:** `00_comun_contrato_y_reglas.md`.

**Indicadores asociados**
- EA4: 3.1.1 Interfaz y flujos funcionales (15%) y los requisitos de accesibilidad y resultados oftalmológicos del anexo.
- EA5: 3.1.1 Funcionamiento (12%) y 3.1.3 Modificación en vivo (40%).

**La accesibilidad es el requisito esencial de esta app.** En la defensa deben mostrar un recorrido completo **con TalkBack y texto ampliado**: iniciar sesión → historial → seleccionar un examen → ver su información, y también el PDF y la imagen con zoom.

---

## 1. Qué debes entregar

- [ ] Todas las pantallas del flujo por rol, con Material 3 y navegación estable.
- [ ] Formularios validados con mensajes **anunciables** por TalkBack.
- [ ] Visor de **PDF** con `PdfRenderer`.
- [ ] Visor de **imágenes** (retina, topografía, campimetría simuladas) con Coil y zoom/desplazamiento.
- [ ] Banner de estado de conexión y sincronización (lo alimenta el integrante 2).
- [ ] Pantalla de la API externa (pacientes de prueba) en coordinación con el integrante 2.
- [ ] Evidencia de accesibilidad: capturas, pruebas con TalkBack, tamaño de fuente aumentado.

---

## 2. Pantallas a completar

| Pantalla | Rol | Estado actual |
|---|---|---|
| Login | todos | Existe; se conecta al backend (integrante 2) |
| Inicio por rol (dashboards) | todos | Existen; Tecnólogo aún con texto de relleno |
| Registro/consulta de **paciente** ficticio | Tecnólogo | **Falta** |
| Registro de **examen** con nuevos campos (ojo, profesional, sucursal, fecha/hora) | Tecnólogo | Existe, hay que ampliarlo |
| Carga de documento | Tecnólogo | Existe como selector de archivos |
| Historial y búsqueda con filtros | Médico, Paciente | Buscador existe; Paciente solo ve su historial |
| Detalle del examen + observaciones + estado | Médico | Existe |
| Detalle de solo lectura | Paciente | Existe como resumen |
| **Visor PDF** | todos | **Falta** |
| **Visor de imagen con zoom** | todos | **Falta** |
| Pacientes de prueba (API externa) | Médico | **Falta** |

Mientras el integrante 2 termina el Repository, trabaja contra los ViewModels actuales; cuando llegue la capa nueva, solo cambian las fuentes de datos.

---

## 3. Visor de PDF con PdfRenderer

El integrante 2 te entrega un `File` ya descargado (así funciona también sin conexión).

```kotlin
@Composable
fun VisorPdf(archivo: File, modifier: Modifier = Modifier) {
    var paginas by remember { mutableStateOf<List<ImageBitmap>>(emptyList()) }
    LaunchedEffect(archivo) {
        paginas = withContext(Dispatchers.IO) { renderizarPdf(archivo) }
    }
    LazyColumn(modifier) {
        itemsIndexed(paginas) { i, bmp ->
            Image(
                bitmap = bmp,
                contentDescription = "Página ${i + 1} de ${paginas.size} del informe",
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
        }
    }
}

private fun renderizarPdf(archivo: File): List<ImageBitmap> {
    val pfd = ParcelFileDescriptor.open(archivo, ParcelFileDescriptor.MODE_READ_ONLY)
    PdfRenderer(pfd).use { renderer ->
        return List(renderer.pageCount) { i ->
            renderer.openPage(i).use { page ->
                val bmp = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                bmp.eraseColor(android.graphics.Color.WHITE)
                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bmp.asImageBitmap()
            }
        }
    }
}
```

- Un PDF escaneado **no es legible por TalkBack**. Por eso cada documento tiene una `descripcionAccesible` (texto que viene del backend) que debes mostrar **como texto** junto al visor: «Resumen del informe: …». Es lo que exige la pauta: «información textual accesible».
- Pensando en PDFs largos: renderiza solo las páginas visibles si hace falta (primero hazlo simple y mide).
- Maneja el error de un PDF dañado o ausente con un mensaje, sin cerrar la app.

---

## 4. Visor de imágenes con Coil y zoom

- Dependencias: `io.coil-kt.coil3:coil-compose` y, para zoom, **Telephoto** (`me.saket.telephoto:zoomable-image-coil3`), que es la librería que menciona la pauta. Confirma las versiones vigentes y que correspondan a Coil 3.
- Como el integrante 2 entrega el archivo local, el modelo es un `File`, sin necesidad de autenticar la carga:

```kotlin
@Composable
fun VisorImagen(archivo: File, descripcion: String, modifier: Modifier = Modifier) {
    ZoomableAsyncImage(
        model = archivo,
        contentDescription = descripcion,      // «Imagen simulada de retina, ojo derecho»
        modifier = modifier.fillMaxSize()
    )
}
```

- Si Telephoto da problemas, una alternativa válida es `Modifier.transformable` con `Image`/`AsyncImage` (zoom y desplazamiento con estado propio).
- Agrega **botones accesibles de acercar y alejar** (≥48 dp, con `contentDescription`): los gestos de pellizco no son usables por todas las personas, y con TalkBack hace falta una alternativa.
- Toda imagen lleva `contentDescription` significativo (viene de `descripcionAccesible`); las decorativas usan `null`.
- Contenido **sintético**: nada de imágenes clínicas reales.

---

## 4b. Lectura por voz (opcional, suma puntos de calidad)

`TextToSpeech` para leer en voz alta el resultado y las observaciones. Es opcional según el docente; agrégalo solo cuando todo lo obligatorio esté listo.

---

## 5. Lista de accesibilidad (WCAG 2.2 AA) para aplicar en todas las pantallas

**Contraste**
- [ ] Texto normal con contraste ≥ 4,5:1; texto grande y componentes ≥ 3:1. Verifícalo con un verificador de contraste o con el *Accessibility Scanner* de Android.
- [ ] Revisa especialmente texto gris sobre fondo claro y los colores de la paleta (`#3A4BBE`, `#3F8EFC`, `#20A39E`) sobre sus fondos.
- [ ] Opcional: tema de **alto contraste** con una preferencia guardada en DataStore.

**Texto ampliable**
- [ ] Todo el texto en `sp`; nada con altura fija que corte el contenido.
- [ ] Probar con **Tamaño de fuente = máximo** y **Tamaño de pantalla grande** en los ajustes del teléfono: ninguna función debe perderse y las pantallas deben poder desplazarse (`verticalScroll`/`LazyColumn`).

**Controles táctiles**
- [ ] Botones, íconos clicables, chips y filas con área ≥ 48 × 48 dp (`Modifier.minimumInteractiveComponentSize()` o `sizeIn(minWidth = 48.dp, minHeight = 48.dp)`).

**TalkBack y semántica**
- [ ] `contentDescription` en íconos e imágenes (y `null` en los decorativos).
- [ ] Los títulos de sección con `Modifier.semantics { heading() }`.
- [ ] Orden de lectura lógico (revisa con TalkBack y, si hace falta, `traversalIndex`).
- [ ] Combina elementos de una tarjeta con `Modifier.semantics(mergeDescendants = true) {}` y una descripción completa: «Campimetría, ojo derecho, 20 de noviembre, estado Autorizado».
- [ ] Los campos de texto tienen `label`; los errores van en `supportingText` con `isError = true`, y se anuncian con `Modifier.semantics { error("mensaje") }` o una región `liveRegion`.
- [ ] Cambios de estado importantes (guardado, «Modo offline») anunciados con `liveRegion = LiveRegionMode.Polite`.

**No depender solo del color**
- [ ] Los estados (Pendiente / Revisado / Autorizado) y los errores llevan **texto o ícono**, no solo un color distinto.

**Navegación**
- [ ] Botón «Volver» con descripción, sin trampas de foco, y diálogos que se pueden cerrar.

---

## 6. Prueba de TalkBack (guion para el día de la defensa y las capturas)

1. Activar TalkBack (Ajustes → Accesibilidad) y subir el tamaño de fuente al máximo.
2. Login: tocar usuario y contraseña, escuchar las etiquetas, provocar un error y confirmar que se **lee el mensaje**.
3. Entrar, abrir el historial y navegar tarjeta por tarjeta; cada una debe leerse completa y en orden.
4. Abrir un examen: se leen todos sus datos, el estado y las observaciones.
5. Abrir el PDF y la imagen: se lee la descripción accesible; probar los botones de zoom.
6. Probar el banner offline: se anuncia el cambio.

Anota y corrige lo que no se lea bien. Toma capturas del recorrido para `docs/evidencias/`.

---

## 7. Tareas por orden de prioridad

1. [ ] Revisar el contraste de la paleta y los tamaños de fuente en las pantallas actuales (cambios rápidos con mucho impacto).
2. [ ] Agregar `contentDescription`, `heading()` y áreas de 48 dp en las pantallas existentes.
3. [ ] Pantallas de paciente (alta y consulta) y ampliación del formulario de examen.
4. [ ] Visor PDF.
5. [ ] Visor de imagen con zoom y botones de acercar y alejar.
6. [ ] Historial y detalle accesibles; paciente solo con sus exámenes.
7. [ ] Banner de «Modo offline / Sincronización pendiente» (con el integrante 2).
8. [ ] Pantalla de la API externa (con el integrante 2).
9. [ ] Prueba completa con TalkBack + texto ampliado, y capturas para las evidencias.
10. [ ] Con el integrante 4: el **Compose UI Test** del formulario (datos inválidos → mensaje → acción válida).

---

## 8. Definición de terminado

- [ ] Todo el flujo de cada rol se completa **solo con TalkBack**, con texto en el tamaño máximo.
- [ ] Ninguna pantalla pierde funciones ni corta texto con la fuente ampliada.
- [ ] El PDF y la imagen se ven, hacen zoom y tienen texto alternativo.
- [ ] Los controles táctiles miden al menos 48 dp.
- [ ] Hay capturas de la prueba de accesibilidad en `docs/evidencias/`.

---

## 9. Qué debes poder defender (15 min, sin IA)

- Demostrar el flujo con TalkBack y texto ampliado, sin perder funciones.
- Explicar qué hace cada medida: contraste, `sp`, 48 dp, `semantics`, `contentDescription`, `liveRegion`, y por qué el color no es el único indicador.
- Explicar cómo funciona `PdfRenderer` (abrir, renderizar página a `Bitmap`, cerrar) y cómo funcionan Coil y el zoom.
- Explicar cómo una pantalla Compose observa el estado del ViewModel y por qué la UI no llama a Retrofit ni a Room.
- **Cambio en vivo (40%):** mostrar el campo nuevo en la pantalla (en lectura y en edición), con su etiqueta accesible, y comprobar el dato desde el teléfono. Practica la cadena completa con los integrantes 1 y 2.
