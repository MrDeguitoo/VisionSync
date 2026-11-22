package com.duoc.visionsyncc1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.ui.components.EstadoConexion
import com.duoc.visionsyncc1.viewmodel.AppViewModelProvider
import com.duoc.visionsyncc1.viewmodel.ExamenesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscadorScreen(
    onExamenSeleccionado: (Int) -> Unit,
    onVolver: () -> Unit,
    viewModel: ExamenesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val busqueda by viewModel.busqueda.collectAsState()
    val tipoFiltro by viewModel.tipoFiltro.collectAsState()
    val examenes by viewModel.examenesFiltrados.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscador de Exámenes") },
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar
            SearchBar(
                value = busqueda,
                onValueChange = { viewModel.onBusquedaCambiada(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                trailingIcon = {
                    if (busqueda.isNotBlank()) {
                        IconButton(onClick = { viewModel.onBusquedaCambiada("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                placeholder = "Buscar por nombre o RUT del paciente..."
            )

            // Filter Chips
            FilterChipsRow(
                selectedTipo = tipoFiltro,
                onTipoSeleccionado = viewModel::onTipoFiltroSeleccionado
            )

            // Exam List
            if (examenes.isEmpty()) {
                EstadoVacioCard(
                    message = if (busqueda.isNotBlank() || tipoFiltro != null) {
                        "No se encontraron exámenes con los filtros actuales."
                    } else {
                        "No hay exámenes registrados."
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(examenes, key = { it.id }) { examen ->
                        ExamenCard(
                            examen = examen,
                            onClick = { onExamenSeleccionado(examen.id) }
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
    leadingIcon: @Composable () -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = { Text(placeholder) },
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
private fun FilterChipsRow(
    selectedTipo: String?,
    onTipoSeleccionado: (String?) -> Unit
) {
    val tipos = listOf("Retinografía", "Campimetría", "OCT", "Topografía", "Biometría")

    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedTipo == null,
            onClick = { onTipoSeleccionado(null) },
            label = { Text("Todos") },
            leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = "Filtro") }
        )

        tipos.forEach { tipo ->
            FilterChip(
                selected = selectedTipo == tipo,
                onClick = { onTipoSeleccionado(tipo) },
                label = { Text(tipo) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamenCard(
    examen: ExamenEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
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
                text = examen.tipoExamen,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Paciente: ${examen.pacienteNombre}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "RUT: ${examen.pacienteRut}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Fecha: ${examen.fecha}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Estado: ${examen.estado}",
                style = MaterialTheme.typography.bodySmall,
                color = when (examen.estado) {
                    "Autorizado" -> MaterialTheme.colorScheme.tertiary
                    "Revisado" -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun EstadoVacioCard(message: String) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Sin resultados",
                style = MaterialTheme.typography.titleMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}