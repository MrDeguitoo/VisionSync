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
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.navigation.Rutas
import com.duoc.visionsyncc1.ui.components.EstadoConexion
import com.duoc.visionsyncc1.viewmodel.AppViewModelProvider
import com.duoc.visionsyncc1.viewmodel.ExamenesViewModel
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.People

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscadorScreen(
    onExamenSeleccionado: (Int) -> Unit,
    onVolver: () -> Unit,
    navController: NavController,
    viewModel: ExamenesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val busqueda by viewModel.busqueda.collectAsState()
    val tipoFiltro by viewModel.tipoFiltro.collectAsState()
    val estadoFiltro by viewModel.estadoFiltro.collectAsState()
    val sucursalFiltro by viewModel.sucursalFiltro.collectAsState()
    val fechaInicio by viewModel.fechaInicio.collectAsState()
    val fechaFin by viewModel.fechaFin.collectAsState()
    val examenes by viewModel.examenesFiltrados.collectAsState()

    // Variables locales para los campos de fecha
    var fechaInicioInput by remember { mutableStateOf<String>(fechaInicio) }
    var fechaFinInput by remember { mutableStateOf<String>(fechaFin) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscador de Exámenes", modifier = Modifier.semantics { heading() }) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate(Rutas.PACIENTES_PRUEBA) },
                        modifier = Modifier.semantics { contentDescription = "Ver pacientes de prueba (API externa)" }
                    ) {
                        Icon(Icons.Default.People, contentDescription = null)
                    }
                    EstadoConexion()
                }
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
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (busqueda.isNotBlank()) {
                        IconButton(onClick = { viewModel.onBusquedaCambiada("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                placeholder = "Buscar por nombre o RUT del paciente..."
            )

            // Filter Chips - Tipo de Examen
            FilterChipsRow(
                selectedTipo = tipoFiltro,
                onTipoSeleccionado = viewModel::onTipoFiltroSeleccionado
            )

            // Filter Chips - Estado
            EstadoFilterChipsRow(
                selectedEstado = estadoFiltro,
                onEstadoSeleccionado = viewModel::onEstadoFiltroSeleccionado
            )

            // Filter Chips - Sucursal
            SucursalFilterChipsRow(
                selectedSucursal = sucursalFiltro,
                onSucursalSeleccionado = viewModel::onSucursalFiltroSeleccionado
            )

            // Fecha Range Picker
            FechaRangePicker(
                fechaInicio = fechaInicioInput,
                fechaFin = fechaFinInput,
                onFechaInicioChange = { fechaInicioInput = it },
                onFechaFinChange = { fechaFinInput = it },
                onAplicar = { inicio, fin ->
                    viewModel.onFechaRangoCambiado(inicio, fin)
                },
                onLimpiar = {
                    fechaInicioInput = ""
                    fechaFinInput = ""
                    viewModel.onFechaRangoCambiado("", "")
                }
            )

            // Exam List
            if (examenes.isEmpty()) {
                EstadoVacioCard(
                    message = if (busqueda.isNotBlank() || tipoFiltro != null || estadoFiltro != null || sucursalFiltro != null || fechaInicio.isNotBlank() || fechaFin.isNotBlank()) {
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
            leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = null) }
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
            modifier = Modifier
                .padding(16.dp)
                .clearAndSetSemantics {
                    contentDescription = "${examen.tipoExamen}. Paciente ${examen.pacienteNombre}. " +
                            "RUT ${examen.pacienteRut}. Fecha ${examen.fecha}. Estado ${examen.estado}."
                },
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
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun EstadoFilterChipsRow(
    selectedEstado: String?,
    onEstadoSeleccionado: (String?) -> Unit
) {
    val estados = listOf("Pendiente", "Validado", "Entregado", "Observado", "Actualizado")

    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedEstado == null,
            onClick = { onEstadoSeleccionado(null) },
            label = { Text("Todos") },
            leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = "Filtro estado") }
        )

        estados.forEach { estado ->
            FilterChip(
                selected = selectedEstado == estado,
                onClick = { onEstadoSeleccionado(estado) },
                label = { Text(estado) }
            )
        }
    }
}

@Composable
private fun SucursalFilterChipsRow(
    selectedSucursal: String?,
    onSucursalSeleccionado: (String?) -> Unit
) {
    // Hardcoded common sucursales for now - could be dynamic from data later
    val sucursales = listOf("Sede Central", "Sucursal Norte", "Sucursal Sur", "Sucursal Este", "Sucursal Oeste")

    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedSucursal == null,
            onClick = { onSucursalSeleccionado(null) },
            label = { Text("Todas") },
            leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = "Filtro sucursal") }
        )

        sucursales.forEach { sucursal ->
            FilterChip(
                selected = selectedSucursal == sucursal,
                onClick = { onSucursalSeleccionado(sucursal) },
                label = { Text(sucursal) }
            )
        }
    }
}

@Composable
private fun FechaRangePicker(
    fechaInicio: String,
    fechaFin: String,
    onFechaInicioChange: (String) -> Unit,
    onFechaFinChange: (String) -> Unit,
    onAplicar: (String, String) -> Unit,
    onLimpiar: () -> Unit
) {
    val tieneFechas = fechaInicio.isNotBlank() && fechaFin.isNotBlank()

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Filtro por Fecha",
                style = MaterialTheme.typography.titleSmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = fechaInicio,
                    onValueChange = onFechaInicioChange,
                    label = { Text("Desde (dd/MM/yyyy)") },
                    placeholder = { Text("dd/MM/yyyy") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = fechaFin,
                    onValueChange = onFechaFinChange,
                    label = { Text("Hasta (dd/MM/yyyy)") },
                    placeholder = { Text("dd/MM/yyyy") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            if (tieneFechas) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onAplicar(fechaInicio, fechaFin) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Aplicar")
                    }

                    OutlinedButton(
                        onClick = onLimpiar,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Limpiar")
                    }
                }
            } else {
                Button(
                    onClick = { onAplicar(fechaInicio, fechaFin) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Aplicar filtro de fecha")
                }
            }
        }
    }
}