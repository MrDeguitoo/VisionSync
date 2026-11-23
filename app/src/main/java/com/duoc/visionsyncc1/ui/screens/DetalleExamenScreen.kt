package com.duoc.visionsyncc1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.navigation.Rutas
import com.duoc.visionsyncc1.ui.components.EstadoConexion
import com.duoc.visionsyncc1.viewmodel.AppViewModelProvider
import com.duoc.visionsyncc1.viewmodel.ExamenesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleExamenScreen(
    examenId: Int,
    onVolver: () -> Unit,
    onExamenAutorizado: () -> Unit,
    viewModel: ExamenesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val examenes by viewModel.examenesFiltrados.collectAsState()
    val examen = examenes.find { it.id == examenId }
    val navController = rememberNavController()

    var observaciones by remember { mutableStateOf("") }
    var mostrarError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Examen", modifier = Modifier.semantics { heading() }) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = { EstadoConexion() }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (examen == null) {
                Text(
                    text = "Examen no encontrado.",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                val e = examen!!

                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Información del examen
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = e.tipoExamen,
                                modifier = Modifier.semantics { heading() },
                                style = MaterialTheme.typography.titleLarge
                            )
                            Divider()
                            InfoRow("Paciente", e.pacienteNombre)
                            InfoRow("RUT", e.pacienteRut)
                            InfoRow("Fecha", e.fecha)
                            e.archivoUri?.let { uri ->
                                val esPdf = uri.lowercase().endsWith(".pdf")
                                val esImagen = uri.lowercase().matches(Regex(".*\\.(jpg|jpeg|png|bmp|webp)$"))
                                val descripcion = e.descripcionAccesible.ifBlank { "${e.tipoExamen}, ${if (esPdf) "informe PDF" else "imagen médica"}" }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Archivo adjunto:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Disponible",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        IconButton(
                                            onClick = {
                                                val ruta = if (esPdf) Rutas.VISOR_PDF else Rutas.VISOR_IMAGEN
                                                navController.navigate("$ruta?archivoUri=${Uri.encode(uri)}&descripcion=${Uri.encode(descripcion)}")
                                            },
                                            modifier = Modifier.semantics { contentDescription = if (esPdf) "Ver informe PDF" else "Ver imagen médica" }
                                        ) {
                                            Icon(
                                                imageVector = if (esPdf) Icons.Default.PictureAsPdf else Icons.Default.Image,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }
                            } ?: InfoRow("Archivo adjunto", "Sin archivo")
                            InfoRow("Estado actual", e.estado)
                        }
                    }

                    // Observaciones médicas
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Observaciones Médicas",
                                modifier = Modifier.semantics { heading() },
                                style = MaterialTheme.typography.titleMedium
                            )

                            OutlinedTextField(
                                value = observaciones,
                                onValueChange = {
                                    observaciones = it
                                    if (mostrarError && it.trim().isNotBlank()) {
                                        mostrarError = false
                                    }
                                },
                                label = { Text("Escribe tus observaciones...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics {
                                        if (mostrarError) error("Las observaciones no pueden estar vacías.")
                                    },
                                minLines = 4,
                                maxLines = 6,
                                singleLine = false,
                                isError = mostrarError
                            )

                            if (mostrarError) {
                                Text(
                                    text = "Las observaciones no pueden estar vacías.",
                                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    // Botón Autorizar
                    if (e.estado != "Autorizado") {
                        Button(
                            onClick = {
                                if (observaciones.trim().isEmpty()) {
                                    mostrarError = true
                                } else {
                                    viewModel.autorizarExamen(e, observaciones, onExamenAutorizado)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                            )
                        ) {
                            Text("Autorizar Examen", style = MaterialTheme.typography.titleMedium)
                        }
                    } else {
                        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Examen ya autorizado",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = "Observaciones registradas: ${e.observaciones}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { },
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        // weight(1f): el valor usa el espacio restante y baja de línea
        // en vez de aplastarse cuando la fuente está ampliada.
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}