package com.duoc.visionsyncc1.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.visionsyncc1.viewmodel.AdminViewModel
import com.duoc.visionsyncc1.viewmodel.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCatalogoScreen(
    onVolver: () -> Unit,
    viewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val sucursales by viewModel.sucursales.collectAsState()
    val profesionales by viewModel.profesionales.collectAsState()

    var showSucursalDialog by mutableStateOf(false)
    var sucursalInput by mutableStateOf("")

    var showProfesionalDialog by mutableStateOf(false)
    var profesionalInput by mutableStateOf("")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Sección Sucursales
            CatalogSection(
                title = "Sucursales",
                icon = Icons.Default.Business,
                items = sucursales,
                emptyMessage = "No hay sucursales registradas",
                onAdd = {
                    sucursalInput = ""
                    showSucursalDialog = true
                },
                onEdit = { item ->
                    sucursalInput = item
                    showSucursalDialog = true
                },
                onDelete = { item ->
                    // TODO: Implementar eliminación (requiere validar que no tenga exámenes asociados)
                }
            )

            // Sección Profesionales
            CatalogSection(
                title = "Profesionales",
                icon = Icons.Default.MedicalServices,
                items = profesionales,
                emptyMessage = "No hay profesionales registrados",
                onAdd = {
                    profesionalInput = ""
                    showProfesionalDialog = true
                },
                onEdit = { item ->
                    profesionalInput = item
                    showProfesionalDialog = true
                },
                onDelete = { item ->
                    // TODO: Implementar eliminación
                }
            )
        }
    }

    if (showSucursalDialog) {
        CatalogDialog(
            title = "Nueva Sucursal",
            input = sucursalInput,
            onInputChange = { sucursalInput = it },
            onConfirm = {
                // TODO: Guardar sucursal (requiere tabla sucursales o usar valores en ExamenEntity)
                showSucursalDialog = false
            },
            onDismiss = { showSucursalDialog = false }
        )
    }

    if (showProfesionalDialog) {
        CatalogDialog(
            title = "Nuevo Profesional",
            input = profesionalInput,
            onInputChange = { profesionalInput = it },
            onConfirm = {
                // TODO: Guardar profesional
                showProfesionalDialog = false
            },
            onDismiss = { showProfesionalDialog = false }
        )
    }
}

@Composable
private fun CatalogSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<String>,
    emptyMessage: String,
    onAdd: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                OutlinedButton(onClick = onAdd) {
                    Text("Agregar")
                }
            }
            if (items.isEmpty()) {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(onClick = { onEdit(item) }) {
                                    Text("Editar")
                                }
                                OutlinedButton(
                                    onClick = { onDelete(item) },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogDialog(
    title: String,
    input: String,
    onInputChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = onInputChange,
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
            TextButton(onClick = { onConfirm() }) {
                Text("Guardar")
            }
        }
    )
}