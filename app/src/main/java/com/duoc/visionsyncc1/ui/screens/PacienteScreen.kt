package com.duoc.visionsyncc1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.min
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.visionsyncc1.navigation.Rutas
import com.duoc.visionsyncc1.ui.components.EstadoConexion
import com.duoc.visionsyncc1.viewmodel.AppViewModelProvider
import com.duoc.visionsyncc1.viewmodel.PacienteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacienteScreen(
    onVolver: () -> Unit,
    onPacienteSeleccionado: (String, String) -> Unit, // nombre, rut
    viewModel: PacienteViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val pacientes by viewModel.pacientes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pacientes", modifier = Modifier.semantics { heading() }) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = { EstadoConexion() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección: Registrar nuevo paciente
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Registrar Nuevo Paciente",
                        modifier = Modifier.semantics { heading() },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Nombre
                    OutlinedTextField(
                        value = uiState.nombre,
                        onValueChange = { viewModel.onNombreChange(it) },
                        label = { Text("Nombre Completo *") },
                        placeholder = { Text("Ej. Juan Pérez González") },
                        isError = uiState.nombreError != null,
                        supportingText = { uiState.nombreError?.let { Text(it) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { uiState.nombreError?.let { error(it) } }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // RUT
                    OutlinedTextField(
                        value = uiState.rut,
                        onValueChange = { viewModel.onRutChange(it) },
                        label = { Text("RUT *") },
                        placeholder = { Text("12.345.678-9") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = RutVisualTransformation(),
                        isError = uiState.rutError != null,
                        supportingText = { uiState.rutError?.let { Text(it) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { uiState.rutError?.let { error(it) } }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.registrarPaciente() },
                        enabled = uiState.nombreError == null && uiState.rutError == null &&
                                uiState.nombre.isNotBlank() && uiState.rut.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar Paciente", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            Divider()

            // Sección: Buscar paciente existente
            Text(
                text = "Buscar Paciente Existente",
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { heading() },
                style = MaterialTheme.typography.titleMedium
            )

            SearchBar(
                value = uiState.busqueda,
                onValueChange = { viewModel.onBusquedaChange(it) },
                placeholder = "Buscar por nombre o RUT..."
            )

            if (pacientes.isEmpty() && uiState.busqueda.isNotBlank()) {
                Text(
                    text = "No se encontraron pacientes.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else if (pacientes.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pacientes) { paciente ->
                        PacienteCard(
                            nombre = paciente.nombre,
                            rut = paciente.rut,
                            onClick = { onPacienteSeleccionado(paciente.nombre, paciente.rut) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (value.isNotBlank()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Limpiar búsqueda")
                }
            }
        },
        label = { Text(placeholder) },
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
private fun PacienteCard(
    nombre: String,
    rut: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Paciente $nombre, RUT $rut. Toca para seleccionar." }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = nombre,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "RUT: $rut",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Transformación visual para formatear RUT mientras se escribe: 12.345.678-9
 */
class RutVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val plainText = text.text
        val digits = plainText.filter { it.isDigit() }
        val formatted = StringBuilder()
        for (i in digits.indices.reversed()) {
            val posFromEnd = digits.length - i
            formatted.insert(0, digits[i])
            if (posFromEnd % 3 == 0 && i > 0) {
                formatted.insert(0, '.')
            }
        }
        if (digits.length >= 2) {
            val cuerpo = formatted.substring(0, formatted.length - 1)
            val dv = formatted[formatted.length - 1]
            formatted.replace(0, formatted.length, "$cuerpo-$dv")
        }
        val formattedText = AnnotatedString(formatted.toString())
        return TransformedText(
            text = formattedText,
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    // Simple mapping: clamp to formatted text length
                    return min(offset, formattedText.length)
                }
                override fun transformedToOriginal(offset: Int): Int {
                    return min(offset, plainText.length)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPacienteScreen() {
    androidx.compose.material3.MaterialTheme {
        PacienteScreen(
            onVolver = {},
            onPacienteSeleccionado = { _, _ -> }
        )
    }
}