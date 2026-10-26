package com.duoc.visionsyncc1.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.visionsyncc1.viewmodel.GuardadoState
import com.duoc.visionsyncc1.viewmodel.RegistroExamenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroExamenScreen(
    onVolver: () -> Unit,
    onGuardadoExitoso: () -> Unit,
    viewModel: RegistroExamenViewModel = viewModel()
) {
    val context = LocalContext.current
    val formState by viewModel.form.collectAsState()
    val guardadoState by viewModel.guardado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Recurso Nativo 1: Storage Access Framework (SAF) con OpenDocument
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.onArchivoSeleccionado(it.toString())
        }
    }

    LaunchedEffect(guardadoState) {
        when (guardadoState) {
            is GuardadoState.Exito -> {
                snackbarHostState.showSnackbar("Examen registrado exitosamente")
                viewModel.limpiarFormulario()
                onGuardadoExitoso()
            }
            is GuardadoState.Error -> {
                snackbarHostState.showSnackbar((guardadoState as GuardadoState.Error).mensaje)
                viewModel.reiniciarEstadoGuardado()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Examen Oftalmológico") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Datos del Paciente y Estudio",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Campo Nombre
            OutlinedTextField(
                value = formState.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre Completo *") },
                placeholder = { Text("Ej. Juan Pérez González") },
                isError = formState.nombreError != null,
                supportingText = { formState.nombreError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo RUT
            OutlinedTextField(
                value = formState.rut,
                onValueChange = { viewModel.onRutChange(it) },
                label = { Text("RUT del Paciente *") },
                placeholder = { Text("12.345.678-9") },
                isError = formState.rutError != null,
                supportingText = { formState.rutError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            // Selector Tipo de Examen (Material 3 Exposed Dropdown)
            var expanded by remember { mutableStateOf(false) }
            val tiposExamen = listOf(
                "Campimetría Computarizada",
                "Topografía Corneal",
                "Agudeza Visual y Refracción",
                "Tomografía de Coherencia Óptica (OCT)",
                "Tonometría y Curva de Tensión"
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = formState.tipoExamen,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de Examen *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    isError = formState.tipoError != null,
                    supportingText = { formState.tipoError?.let { Text(it) } },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    tiposExamen.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo) },
                            onClick = {
                                viewModel.onTipoChange(tipo)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Card Selector de Archivo Nativo
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Informe o Archivo Clínico *",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { launcher.launch(arrayOf("application/pdf", "image/*")) }
                    ) {
                        Text("Adjuntar Informe (PDF o Imagen)")
                    }

                    if (formState.archivoUri != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Archivo seleccionado: ${formState.archivoUri}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (formState.archivoError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formState.archivoError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de Envío
            Button(
                onClick = { viewModel.guardar() },
                enabled = guardadoState !is GuardadoState.Guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (guardadoState is GuardadoState.Guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Registrar Examen")
                }
            }
        }
    }
}