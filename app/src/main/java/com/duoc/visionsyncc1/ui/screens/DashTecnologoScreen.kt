package com.duoc.visionsyncc1.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashTecnologoScreen(
    onNuevoExamen: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel del Tecnólogo") },
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
            // Tarjeta informativa institucional
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Bienvenido/a",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Desde este panel puedes registrar nuevos exámenes oftalmológicos y asociar informes médicos con archivos adjuntos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón principal de acción
            Button(
                onClick = onNuevoExamen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("+ Registrar Nuevo Examen", style = MaterialTheme.typography.titleMedium)
            }

            // Espacio reservado para histórico reactivo de Room
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Actividad reciente",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Una vez integrado el módulo Room del Integrante B, aquí se observará el listado en vivo de los últimos exámenes ingresados.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}