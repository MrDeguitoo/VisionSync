package com.duoc.visionsyncc1.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val auditoria by viewModel.auditoria.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Auditoría del Sistema") },
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
                .fillMaxSize()
        ) {
            if (auditoria.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Assignment,
                    title = "No hay registros de auditoría",
                    message = "Las acciones del sistema aparecerán aquí automáticamente"
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(auditoria, key = { it.id }) { consulta ->
                        AuditoriaCard(consulta = consulta)
                    }
                }
            }
        }
    }
}

@Composable
private fun AuditoriaCard(consulta: ConsultaEntity) {
    val (icon, color) = when (consulta.accion) {
        ConsultaEntity.ACCION_CREADO -> Icons.Default.Add to MaterialTheme.colorScheme.primary
        ConsultaEntity.ACCION_VALIDADO -> Icons.Default.CheckCircle to MaterialTheme.colorScheme.tertiary
        ConsultaEntity.ACCION_ENTREGADO -> Icons.Default.Visibility to MaterialTheme.colorScheme.secondary
        ConsultaEntity.ACCION_OBSERVADO -> Icons.Default.Info to MaterialTheme.colorScheme.warning
        ConsultaEntity.ACCION_ACTUALIZADO -> Icons.Default.Edit to MaterialTheme.colorScheme.primary
        ConsultaEntity.ACCION_ELIMINADO -> Icons.Default.Delete to MaterialTheme.colorScheme.error
        else -> Icons.Default.Assignment to MaterialTheme.colorScheme.onSurfaceVariant
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color.copy(alpha = 0.15f), androidx.compose.foundation.shape.CircleShape)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = consulta.accion,
                            tint = color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = consulta.accion,
                            style = MaterialTheme.typography.titleSmall,
                            color = color
                        )
                        Text(
                            text = "Examen #${consulta.examenId} • ${consulta.rolUsuario}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = consulta.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (consulta.estadoAnterior != null || consulta.estadoNuevo != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    consulta.estadoAnterior?.let { anterior ->
                        Column {
                            Text(
                                text = "Estado anterior",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = anterior,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    consulta.estadoNuevo?.let { nuevo ->
                        Column {
                            Text(
                                text = "Estado nuevo",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = nuevo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            consulta.comentario?.let { comentario ->
                Text(
                    text = "\"$comentario\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Text(
                text = "Usuario ID: ${consulta.usuarioId ?: "Sistema"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
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