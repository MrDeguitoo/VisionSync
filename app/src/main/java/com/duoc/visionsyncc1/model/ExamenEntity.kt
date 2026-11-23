package com.duoc.visionsyncc1.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "examenes")
data class ExamenEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pacienteNombre: String,
    val pacienteRut: String,
    val tipoExamen: String,
    val ojo: String = "", // Derecho / Izquierdo / Ambos
    val profesional: String = "",
    val sucursal: String = "",
    val fecha: String, // Formato: yyyy-MM-dd HH:mm
    val archivoUri: String?,
    val descripcionAccesible: String = "", // Texto alternativo para accesibilidad (PDF/imagen)
    val observaciones: String = "",
    val estado: String = "Pendiente" // Pendiente / Revisado / Autorizado
)