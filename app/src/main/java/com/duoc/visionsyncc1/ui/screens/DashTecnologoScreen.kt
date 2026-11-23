package com.duoc.visionsyncc1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.viewmodel.AppViewModelProvider
import com.duoc.visionsyncc1.viewmodel.DashTecnologoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashTecnologoScreen(
    onNuevoExamen: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: DashTecnologoViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val listaExamenes by viewModel.examenesRecientes.collectAsState()

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
            Button(
                onClick = onNuevoExamen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("+ Registrar Nuevo Examen", style = MaterialTheme.typography.titleMedium)
            }

            Text(
                text = "Exámenes Registrados Recientemente",
                style = MaterialTheme.typography.titleSmall
            )

            if (listaExamenes.isEmpty()) {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Aún no se han registrado exámenes.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(listaExamenes) { examen ->
                        ExamenItemCard(examen = examen)
                    }
                }
            }
        }
    }
}

@Composable
fun ExamenItemCard(examen: ExamenEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = examen.pacienteNombre,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "RUT: ${examen.pacienteRut} | Tipo: ${examen.tipoExamen}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Fecha: ${examen.fecha} | Estado: ${examen.estado}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}