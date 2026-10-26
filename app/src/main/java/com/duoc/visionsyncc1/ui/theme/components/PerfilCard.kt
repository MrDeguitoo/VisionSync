package com.duoc.visionsyncc1.ui.theme.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.visionsyncc1.model.PerfilAcceso

@Composable
fun PerfilCard(
    perfil: PerfilAcceso,
    seleccionado: Boolean,
    onClick: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if (seleccionado) BorderStroke(2.dp, Color(0xFF4F46E5)) else null
    ){
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ){
            Column(modifier = Modifier.weight(1f)){
                Text(
                    text = perfil.etiqueta,
                    fontSize = 11.sp,
                    color = Color(0xFF4F46E5)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = perfil.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = perfil.descripcion,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            RadioButton(
                selected = seleccionado,
                onClick = onClick
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PerfilCardPreview() {
    PerfilCard(
        perfil = PerfilAcceso(
            id = 1,
            etiqueta = "Atención Clínica & Cirugía",
            titulo = "Médico Oftalmólogo",
            descripcion = "Consultas médicas, ficha clínica oftalmológica, cirugías y emisión de recetas ópticas."
        ),
        seleccionado = true,
        onClick = {}
    )
}