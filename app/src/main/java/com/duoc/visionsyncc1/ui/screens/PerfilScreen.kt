package com.duoc.visionsyncc1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.visionsyncc1.ui.theme.components.PerfilCard
import com.duoc.visionsyncc1.viewmodel.PerfilViewModel
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel = viewModel(),
    onPerfilTecnologo: () -> Unit = {},
    onPerfilMedico: () -> Unit = {},
    onPerfilPaciente: () -> Unit = {}
){
    val perfilActivo = viewModel.perfilSeleccionado.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(32.dp))

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Portal Oftalmológico Clínico",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Selecciona tu perfil de acceso para continuar al sistema de salud visual",
        fontSize = 14.sp,
        color = Color.Gray,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    viewModel.perfiles.forEach { perfil ->
        PerfilCard(
            perfil = perfil,
            seleccionado = perfil.id == perfilActivo.value.id,
            onClick = { viewModel.seleccionarPerfil(perfil) }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
    Spacer (modifier = Modifier.weight(1f))

    Button(
        onClick = {
            when (perfilActivo.value.id){
                1 -> onPerfilMedico()
                2 -> onPerfilTecnologo()
                3 -> onPerfilPaciente()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
    ){
        Text(
            text = viewModel.textoBoton() + "  →",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Encriptación Ficha Clínica ° Ley 20.584 / HIPAA",
        fontSize = 11.sp,
        color = Color.Gray
    )
    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = "VisionSync v1.0 ° Sistema seguro de Salud Visual",
        fontSize = 11.sp,
        color = Color.Gray
    )
  }
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    PerfilScreen()
}