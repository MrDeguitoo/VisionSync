package com.duoc.visionsyncc1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.visionsyncc1.model.ConsultaEntity
import com.duoc.visionsyncc1.viewmodel.AdminViewModel
import com.duoc.visionsyncc1.viewmodel.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAuditoriaScreen(
    onVolver: () -> Unit,
    viewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val consultas by viewModel.consultasFlow.collectAsState()
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    val acciones = listOf("VISUALIZAR", "VALIDAR", "ENTREGAR", "OBSERVAR", "ACTUALIZAR", "DESCARGAR", "EXPORTAR")
    val allAcciones = listOf(null) + acciones // null = todos

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Auditoría de Consultas") },
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Filtros
            if (consultas.isNotEmpty()) {
                SingleChoiceFilterChips(
                    options = allAcciones.map { it ?: "TODAS" },
                    selected = selectedFilter ?: "TODAS",
                    onSelected = { label ->
                        selectedFilter = if (label == "TODAS") null else label
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()
                )
            }

            // Lista de consultas
            val filteredConsultas = if (selectedFilter == null) {
                consultas
            } else {
                consultas.filter { it.accion == selectedFilter }
            }

            if (filteredConsultas.isEmpty()) {
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
                            Text("Sin registros de auditoría", style = MaterialTheme.typography.titleMedium)
                            if (selectedFilter != null) {
                                Text("No hay consultas con acción '$selectedFilter'", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                Text("No se han registrado consultas aún", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
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
                    items(filteredConsultas, key = { it.id }) { consulta ->
                        ConsultaCard(consulta = consulta)
                    }
                }
            }
        }
    }
}

@Composable
private fun SingleChoiceFilterChips(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = (selected == option),
                onClick = { onSelected(option) },
                label = { Text(option) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
private fun ConsultaCard(consulta: ConsultaEntity) {
    val actionColor = when (consulta.accion) {
        "VISUALIZAR" -> MaterialTheme.colorScheme.primary
        "VALIDAR" -> MaterialTheme.colorScheme.tertiary
        "ENTREGAR" -> Color(0xFF2E7D32) // Green
        "OBSERVAR" -> Color(0xFFFF8F00) // Orange
        "ACTUALIZAR" -> Color(0xFF1565C0) // Blue
        "DESCARGAR" -> MaterialTheme.colorScheme.secondary
        "EXPORTAR" -> Color(0xFF6A1B9A) // Purple
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Examen #${consulta.examenId}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .wrapContentSize()
                        .background(
                            color = actionColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .border(1.dp, actionColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = consulta.accion,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = actionColor
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Usuario: ${consulta.usuarioNombre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Rol: ${consulta.rol}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fecha: ${consulta.fechaConsulta}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}