package com.duoc.visionsyncc1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.visionsyncc1.network.SyncState
import com.duoc.visionsyncc1.network.SyncStateManager
import com.duoc.visionsyncc1.VisionSyncApp

private val ColorExito = Color(0xFF20A39E)
private val ColorSinConexion = Color(0xFFB3261E)
private val ColorPendiente = Color(0xFFF59E0B)
private val ColorError = Color(0xFFB3261E)

/** Versión que muestra el estado de sincronización detallado desde SyncStateManager. */
@Composable
fun EstadoConexion(modifier: Modifier = Modifier) {
    // Obtener el SyncStateManager desde la Application
    val context = LocalContext.current
    val app = context.applicationContext as VisionSyncApp
    val syncStateManager = app.syncStateManager

    val syncState by syncStateManager.state.collectAsState(initial = SyncState.Offline)
    EstadoConexionPill(syncState, modifier)
}

/** Versión sin estado: sirve para @Preview y pruebas. */
@Composable
fun EstadoConexionPill(syncState: SyncState, modifier: Modifier = Modifier) {
    val (color, texto, descripcion, conectado) = when (syncState) {
        is SyncState.Sincronizado -> MaterialTheme.colorScheme.tertiary to "Sincronizado" to "Estado de conexión: sincronizado" to true
        is SyncState.Pendiente -> MaterialTheme.colorScheme.tertiary to "Sincronización pendiente (${syncState.count})" to "Estado de conexión: sincronización pendiente (${syncState.count})" to true
        is SyncState.Error -> MaterialTheme.colorScheme.error to "Error: ${syncState.mensaje}" to "Estado de conexión: error - ${syncState.mensaje}" to false
        SyncState.Offline -> MaterialTheme.colorScheme.error to "Modo offline" to "Estado de conexión: sin conexión, trabajando sin internet" to false
    }

    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = descripcion
                // Anuncia el cambio sin interrumpir lo que TalkBack está leyendo
                liveRegion = LiveRegionMode.Polite
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(8.dp).background(color, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(
            text = texto,
            color = color, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewConexion() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EstadoConexionPill(SyncState.Sincronizado)
        EstadoConexionPill(SyncState.Pendiente(3))
        EstadoConexionPill(SyncState.Error("Error de red"))
        EstadoConexionPill(SyncState.Offline)
    }
}