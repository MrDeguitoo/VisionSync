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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.visionsyncc1.navigation.Rutas
import com.duoc.visionsyncc1.ui.components.EstadoConexion
import com.duoc.visionsyncc1.viewmodel.AppViewModelProvider
import com.duoc.visionsyncc1.viewmodel.ResumenExamenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenExamenScreen(
    examenId: Int,
    onVolver: () -> Unit,
    viewModel: ResumenExamenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(examenId) { viewModel.cargar(examenId) }

    val examen by viewModel.examen.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen del examen", modifier = Modifier.semantics { heading() }) },
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
            when {
                cargando -> CircularProgressIndicator(
                    Modifier
                        .align(Alignment.Center)
                        .semantics { contentDescription = "Cargando examen" }
                )

                examen == null -> Text(
                    text = "Este examen no está disponible.",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                    style = MaterialTheme.typography.bodyLarge
                )

                else -> {
                    val e = examen!!
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
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
                                    e.tipoExamen,
                                    modifier = Modifier.semantics { heading() },
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text("Paciente: ${e.pacienteNombre}")
                                Text("RUT: ${e.pacienteRut}")
                                Text("Fecha: ${e.fecha}")
                                Text("Estado: ${e.estado}")
                                e.archivoUri?.let { uri ->
                                    val esPdf = uri.lowercase().endsWith(".pdf")
                                    val esImagen = uri.lowercase().matches(Regex(".*\\.(jpg|jpeg|png|bmp|webp)$"))
                                    val descripcion = e.descripcionAccesible.ifBlank { "${e.tipoExamen}, ${if (esPdf) "informe PDF" else "imagen médica"}" }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Archivo adjunto:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            }
                        }

                        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    "Observaciones del médico",
                                    modifier = Modifier.semantics { heading() },
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = e.observaciones.ifBlank { "Sin observaciones registradas." },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}