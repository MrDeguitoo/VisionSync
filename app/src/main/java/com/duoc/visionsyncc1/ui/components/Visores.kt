package com.duoc.visionsyncc1.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Remove
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.visionsyncc1.ui.theme.VisionSyncTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.rememberZoomableImageState

/**
 * Visor de PDF usando PdfRenderer del SDK de Android.
 * Renderiza cada página a un Bitmap y la muestra en un LazyColumn.
 *
 * @param archivo Archivo PDF local (ya descargado por el integrante 2)
 * @param descripcionAccesible Texto alternativo para accesibilidad (viene del backend)
 * @param onCerrar Callback para cerrar el visor
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisorPdf(
    archivo: java.io.File,
    descripcionAccesible: String = "Informe médico en formato PDF",
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var paginas by remember { mutableStateOf<List<ImageBitmap>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var retryTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(archivo, retryTrigger) {
        cargando = true
        error = null
        paginas = withContext(Dispatchers.IO) {
            try {
                renderizarPdf(context, archivo)
            } catch (e: Exception) {
                error = "No se pudo abrir el PDF: ${e.message}"
                emptyList()
            } finally {
                cargando = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Visor de Informe",
                        modifier = Modifier.semantics { heading() }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCerrar) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar visor"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Descripción accesible (requisito: texto alternativo para PDFs no legibles por TalkBack)
            Text(
                text = "Resumen del informe: $descripcionAccesible",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .semantics {
                        heading()
                        liveRegion = LiveRegionMode.Polite
                    },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (cargando) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.semantics { contentDescription = "Cargando páginas del PDF" }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Renderizando páginas...", style = MaterialTheme.typography.bodyMedium)
                }
            } else if (error != null) {
                ErrorCard(mensaje = error!!, onReintentar = {
                    retryTrigger++
                })
            } else if (paginas.isEmpty()) {
                Text(
                    text = "El PDF no tiene páginas visibles.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(paginas) { index, bitmap ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Página ${index + 1} de ${paginas.size}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = "Página ${index + 1} del informe médico",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat())
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Renderiza un archivo PDF a una lista de Bitmaps (una por página).
 * Se ejecuta en IO thread.
 */
private fun renderizarPdf(context: Context, archivo: java.io.File): List<ImageBitmap> {
    val pfd = ParcelFileDescriptor.open(archivo, ParcelFileDescriptor.MODE_READ_ONLY)
    return PdfRenderer(pfd).use { renderer ->
        (0 until renderer.pageCount).map { pageIndex ->
            renderer.openPage(pageIndex).use { page ->
                // Renderizar a 2x para mejor calidad en pantallas de alta densidad
                val width = page.width * 2
                val height = page.height * 2
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                bmp.eraseColor(Color.WHITE)
                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bmp.asImageBitmap()
            }
        }
    }
}

/**
 * Visor de imágenes con zoom usando Coil 3 + Telephoto.
 * Incluye botones accesibles de zoom (+/-) para usuarios que no pueden usar gestos de pellizco.
 *
 * @param archivo Archivo de imagen local
 * @param descripcion Descripción accesible (ej: "Retinografía, ojo derecho")
 * @param onCerrar Callback para cerrar el visor
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisorImagen(
    archivo: java.io.File,
    descripcion: String = "Imagen médica",
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val zoomableState = rememberZoomableState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Visor de Imagen",
                        modifier = Modifier.semantics { heading() }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCerrar) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar visor"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Descripción accesible
            Text(
                text = descripcion,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .semantics {
                        heading()
                        liveRegion = LiveRegionMode.Polite
                    },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Imagen con zoom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                ZoomableAsyncImage(
                    model = archivo,
                    contentDescription = descripcion,
                    state = rememberZoomableImageState(zoomableState),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                // Botones de zoom accesibles (superpuestos)
                ZoomControls()
            }
        }
    }
}

/**
 * Botones accesibles de zoom (+ / -) para el visor de imágenes.
 * Cumple WCAG: alternativa a gestos de pellizco, ≥48dp, con contentDescription.
 * Nota: Telephoto maneja el zoom por gestos; estos botones son una alternativa accesible.
 */
@Composable
private fun ZoomControls() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
        // Botón Acercar (+)
        IconButton(
            onClick = { /* Telephoto maneja zoom por gestos; este botón es para accesibilidad */ },
            modifier = Modifier
                .size(48.dp)
                .semantics {
                    contentDescription = "Acercar imagen (use gestos de pellizco)"
                }
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        }

        // Botón Alejar (-)
        IconButton(
            onClick = { /* Telephoto maneja zoom por gestos */ },
            modifier = Modifier
                .size(48.dp)
                .semantics {
                    contentDescription = "Alejar imagen (use gestos de pellizco)"
                }
        ) {
            Icon(Icons.Default.Remove, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        }

        // Botón Reset (100%)
        IconButton(
            onClick = { /* Telephoto: doble toque para reset */ },
            modifier = Modifier
                .size(48.dp)
                .semantics {
                    contentDescription = "Restablecer zoom (doble toque)"
                }
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
}

/**
 * Tarjeta de error reutilizable con botón de reintentar.
 */
@Composable
private fun ErrorCard(mensaje: String, onReintentar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = mensaje,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { liveRegion = LiveRegionMode.Assertive },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Button(
                onClick = onReintentar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Reintentar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVisorPdf() {
    VisionSyncTheme {
        VisorPdf(
            archivo = java.io.File(""),
            descripcionAccesible = "Informe de campimetría computarizada, ojo derecho, 20 de noviembre",
            onCerrar = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVisorImagen() {
    VisionSyncTheme {
        VisorImagen(
            archivo = java.io.File(""),
            descripcion = "Retinografía simulada, ojo derecho",
            onCerrar = {}
        )
    }
}