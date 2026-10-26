package com.duoc.visionsyncc1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.visionsyncc1.util.observarConexion

private val ColorExito = Color(0xFF20A39E)
private val ColorSinConexion = Color(0xFFB3261E)

/** Versión que se conecta sola al estado de red. Úsala en los dashboards. */
@Composable
fun EstadoConexion(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val conectado by remember(context) { observarConexion(context) }
        .collectAsState(initial = true)
    EstadoConexionPill(conectado, modifier)
}

/** Versión sin estado: sirve para @Preview y pruebas. */
@Composable
fun EstadoConexionPill(conectado: Boolean, modifier: Modifier = Modifier) {
    val color = if (conectado) ColorExito else ColorSinConexion
    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(8.dp).background(color, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(
            text = if (conectado) "Sincronizado" else "Sin conexión",
            color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewConexion() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EstadoConexionPill(true); EstadoConexionPill(false)
    }
}