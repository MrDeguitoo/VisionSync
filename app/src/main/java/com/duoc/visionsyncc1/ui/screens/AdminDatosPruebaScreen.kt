package com.duoc.visionsyncc1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.visionsyncc1.viewmodel.AdminViewModel
import com.duoc.visionsyncc1.viewmodel.AppViewModelProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDatosPruebaScreen(
    onVolver: () -> Unit,
    viewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val generandoDatos by viewModel.generandoDatos.collectAsState()
    val mensajeGeneracion by viewModel.mensajeGeneracion.collectAsState()
    var cantidad by remember { mutableStateOf("20") }
    var cantidadError by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Generar Datos de Prueba") },
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
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tarjeta informativa
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Generador de Datos de Prueba",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Esta herramienta crea exámenes oftalmológicos ficticios para probar la aplicación. Los datos incluyen pacientes, tipos de examen, estados, sucursales y profesionales aleatorios.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Campo cantidad
            OutlinedTextField(
                value = cantidad,
                onValueChange = {
                    cantidad = it.filter { it.isDigit() }
                    cantidadError = ""
                },
                label = { Text("Cantidad de exámenes a generar *") },
                placeholder = { Text("Ej. 20") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = cantidadError.isNotBlank(),
                supportingText = { if (cantidadError.isNotBlank()) Text(cantidadError) },
                modifier = Modifier.fillMaxWidth().width(300.dp)
            )

            // Botón generar
            Button(
                onClick = {
                    val cant = cantidad.toIntOrNull() ?: 0
                    if (cant <= 0) {
                        cantidadError = "Debe ser un número mayor a 0"
                        return@Button
                    }
                    if (cant > 500) {
                        cantidadError = "Máximo 500 exámenes por generación"
                        return@Button
                    }
                    viewModel.generarDatosPrueba(cant)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .width(300.dp)
                    .height(50.dp),
                enabled = !generandoDatos
            ) {
                if (generandoDatos) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        Text("Generando...", style = MaterialTheme.typography.titleMedium)
                    }
                } else {
                    Text("Generar Exámenes", style = MaterialTheme.typography.titleMedium)
                }
            }

            // Mensaje de resultado
            if (mensajeGeneracion.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (generandoDatos) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else if (mensajeGeneracion.contains("correctamente") || mensajeGeneracion.contains("generados")) {
                            MaterialTheme.colorScheme.tertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        }
                    ),
                    modifier = Modifier.fillMaxWidth().width(300.dp)
                ) {
                    Text(
                        text = mensajeGeneracion,
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (generandoDatos) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else if (mensajeGeneracion.contains("correctamente") || mensajeGeneracion.contains("generados")) {
                            MaterialTheme.colorScheme.onTertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        },
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón limpiar (opcional)
            OutlinedButton(
                onClick = {
                    // TODO: Implementar limpieza de exámenes cuando se añada el método al repositorio
                    snackbarMessage = "Función 'Limpiar Todos los Exámenes' no implementada aún"
                },
                modifier = Modifier.fillMaxWidth().width(300.dp).height(50.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Limpiar Todos los Exámenes", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
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
}