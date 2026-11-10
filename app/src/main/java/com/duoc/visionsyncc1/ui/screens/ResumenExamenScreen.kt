package com.duoc.visionsyncc1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen del examen") },
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
                cargando -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                examen == null -> Text(
                    text = "Este examen no está disponible.",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
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
                                Text(e.tipoExamen, style = MaterialTheme.typography.titleLarge)
                                Text("Paciente: ${e.pacienteNombre}")
                                Text("RUT: ${e.pacienteRut}")
                                Text("Fecha: ${e.fecha}")
                                Text("Estado: ${e.estado}")
                            }
                        }

                        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    "Observaciones del médico",
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