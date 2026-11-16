package com.duoc.visionsyncc1.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.visionsyncc1.model.ExamenEntity
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

    var observaciones by remember { mutableStateOf("") }
    var mostrarError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Examen") },
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

                // Launcher para ver el archivo adjunto
                val viewFileLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    // No action needed on result
                }

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
                                style = MaterialTheme.typography.titleLarge
                            )
                            Divider()
                            InfoRow("Paciente", e.pacienteNombre)
                            InfoRow("RUT", e.pacienteRut)
                            InfoRow("Fecha", e.fecha)
                            e.archivoUri?.let { uri ->
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
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Disponible",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    IconButton(onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse(uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        viewFileLauncher.launch(intent)
                                    }) {
                                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Ver archivo adjunto")
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
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                maxLines = 6,
                                singleLine = false,
                                isError = mostrarError
                            )

                            if (mostrarError) {
                                Text(
                                    text = "Las observaciones no pueden estar vacías.",
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
                            enabled = !observaciones.trim().isEmpty(),
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}