package com.duoc.visionsyncc1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.visionsyncc1.viewmodel.AdminViewModel
import com.duoc.visionsyncc1.viewmodel.AppViewModelProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCatalogoScreen(
    onVolver: () -> Unit,
    viewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val tiposExamen by viewModel.tiposExamen.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }
    var nuevoTipo by remember { mutableStateOf("") }
    var tipoError by remember { mutableStateOf("") }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Exámenes") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    nuevoTipo = ""
                    tipoError = ""
                    showDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo tipo")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (tiposExamen.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedCard {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("No hay tipos de examen registrados", style = MaterialTheme.typography.titleMedium)
                            Text("Presiona el botón flotante para agregar uno", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tiposExamen, key = { it }) { tipo ->
                        TipoExamenCard(
                            tipo = tipo,
                            onDelete = {
                                viewModel.eliminarTipo(tipo)
                                snackbarMessage = "Tipo '$tipo' eliminado"
                            }
                        )
                    }
                }
            }
        }
    }

    // Snackbar LaunchedEffect
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            snackbarMessage = null
        }
    }

    // Dialog para nuevo tipo
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nuevo Tipo de Examen") },
            text = {
                OutlinedTextField(
                    value = nuevoTipo,
                    onValueChange = { nuevoTipo = it; tipoError = "" },
                    label = { Text("Nombre del tipo *") },
                    singleLine = true,
                    isError = tipoError.isNotBlank(),
                    supportingText = { if (tipoError.isNotBlank()) Text(tipoError) },
                    modifier = Modifier.padding(16.dp).fillMaxWidth().width(300.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val tipoTrim = nuevoTipo.trim()
                    if (tipoTrim.isEmpty()) {
                        tipoError = "El nombre no puede estar vacío"
                        return@TextButton
                    }
                    if (tiposExamen.any { it.equals(tipoTrim, ignoreCase = true) }) {
                        tipoError = "Este tipo ya existe"
                        return@TextButton
                    }
                    viewModel.agregarTipo(tipoTrim)
                    snackbarMessage = "Tipo '$tipoTrim' agregado"
                    showDialog = false
                }) { Text("Agregar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun TipoExamenCard(
    tipo: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = tipo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}