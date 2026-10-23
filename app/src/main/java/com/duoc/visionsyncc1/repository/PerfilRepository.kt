package com.duoc.visionsyncc1.repository

import com.duoc.visionsyncc1.model.PerfilAcceso

class PerfilRepository{
    fun obtenerPerfiles(): List<PerfilAcceso> {
        return listOf(
            PerfilAcceso(
                id = 1,
                etiqueta = "Atención Clínica & Cirugía",
                titulo = "Médico Oftalmólogo",
                descripcion = "Consultas médicas, ficha clínica oftalmológica, cirugías y emisión de recetas ópticas."
            ),
            PerfilAcceso(
                id = 2,
                etiqueta = "Exámenes & Refracción",
                titulo = "Tecnólogo Médico",
                descripcion = "Campimetría, OCT, tonometría, topografía corneal y refracción clínica instrumental."
            ),
            PerfilAcceso(
                id = 3,
                etiqueta = "Portal Paciente",
                titulo = "Paciente / Portal Usuario",
                descripcion = "Agendamiento de horas, descarga de informes, recetas oftalmológicas y comprobantes."
            )
        )
    }
}