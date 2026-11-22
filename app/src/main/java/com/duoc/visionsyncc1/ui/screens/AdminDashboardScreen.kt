package com.duoc.visionsyncc1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onGestionUsuarios: () -> Unit,
    onCatalogo: () -> Unit,
    onDatosPrueba: () -> Unit,
    onAuditoria: () -> Unit,
    onCerrarSesion: () -> Unit
) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                        text = "Bienvenido, Administrador",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Desde este panel puedes gestionar usuarios, catálogos, generar datos de prueba y revisar la auditoría del sistema.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botones de navegación a cada sección
            AdminCard(
                title = "Gestión de Usuarios",
                subtitle = "Crear, editar roles y eliminar usuarios del sistema",
                icon = Icons.Default.AccountCircle,
                onClick = onGestionUsuarios
            )

            AdminCard(
                title = "Catálogo de Exámenes",
                subtitle = "Administrar tipos de exámenes oftalmológicos disponibles",
                icon = Icons.Default.MedicalInformation,
                onClick = onCatalogo
            )

            AdminCard(
                title = "Generar Datos de Prueba",
                subtitle = "Crear exámenes de prueba para poblar la base de datos",
                icon = Icons.Default.Science,
                onClick = onDatosPrueba
            )

            AdminCard(
                title = "Auditoría de Consultas",
                subtitle = "Ver historial de accesos y acciones sobre exámenes",
                icon = Icons.Default.History,
                onClick = onAuditoria
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón volver al login
            OutlinedButton(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Volver al Login", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun AdminCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize(0f),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            val arrowIcon = Icons.Default.ArrowForward
            arrowIcon.let { arrowIcon ->
                Icon(
                    imageVector = arrowIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}