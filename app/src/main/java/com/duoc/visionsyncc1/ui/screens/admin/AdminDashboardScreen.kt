package com.duoc.visionsyncc1.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.visionsyncc1.viewmodel.AdminViewModel
import com.duoc.visionsyncc1.viewmodel.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onUsuarios: () -> Unit,
    onCatalogo: () -> Unit,
    onDatosPrueba: () -> Unit,
    onAuditoria: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val stats by viewModel.stats.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administrador") },
                actions = {
                    TextButton(onClick = onCerrarSesion) {
                        Text("Salir", color = MaterialTheme.colorScheme.error)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de bienvenida
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dashboard de Administración",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Gestión completa de usuarios, catálogos, datos de prueba y auditoría del sistema.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid de estadísticas
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Resumen del Sistema", style = MaterialTheme.typography.titleSmall)
                }
                item {
                    StatsGrid(stats = stats, cargando = cargando)
                }
                item {
                    Text("Accesos Rápidos", style = MaterialTheme.typography.titleSmall)
                }
                item {
                    QuickActionsGrid(
                        onUsuarios = onUsuarios,
                        onCatalogo = onCatalogo,
                        onDatosPrueba = onDatosPrueba,
                        onAuditoria = onAuditoria
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsGrid(stats: com.duoc.visionsyncc1.viewmodel.AdminDashboardStats, cargando: Boolean) {
    if (cargando) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cargando estadísticas...", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Exámenes",
                value = stats.totalExamenes.toString(),
                icon = Icons.Default.Assignment,
                color = MaterialTheme.colorScheme.primary
            )
            StatCard(
                title = "Pendientes",
                value = stats.pendientes.toString(),
                icon = Icons.Default.ReportProblem,
                color = MaterialTheme.colorScheme.warning
            )
            StatCard(
                title = "Revisados",
                value = stats.revisados.toString(),
                icon = Icons.Default.ListAlt,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Autorizados",
                value = stats.autorizados.toString(),
                icon = Icons.Default.Dashboard,
                color = MaterialTheme.colorScheme.tertiary
            )
            StatCard(
                title = "Observados",
                value = stats.observados.toString(),
                icon = Icons.Default.ReportProblem,
                color = MaterialTheme.colorScheme.error
            )
            StatCard(
                title = "Usuarios",
                value = stats.totalUsuarios.toString(),
                icon = Icons.Default.People,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Auditoría",
                value = stats.totalConsultas.toString(),
                icon = Icons.Default.Science,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionsGrid(
    onUsuarios: () -> Unit,
    onCatalogo: () -> Unit,
    onDatosPrueba: () -> Unit,
    onAuditoria: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionCard(
                title = "Gestión de Usuarios",
                subtitle = "Crear, editar, eliminar usuarios y roles",
                icon = Icons.Default.People,
                onClick = onUsuarios
            )
            ActionCard(
                title = "Catálogo",
                subtitle = "Sucursales, profesionales, tipos de examen",
                icon = Icons.Default.Science,
                onClick = onCatalogo
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionCard(
                title = "Datos de Prueba",
                subtitle = "Generar datos sintéticos para testing",
                icon = Icons.Default.ListAlt,
                onClick = onDatosPrueba
            )
            ActionCard(
                title = "Auditoría",
                subtitle = "Historial completo de acciones del sistema",
                icon = Icons.Default.ReportProblem,
                onClick = onAuditoria
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .fillMaxHeight(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}