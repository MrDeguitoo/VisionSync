package com.duoc.visionsyncc1.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "examenes")
data class ExamenEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pacienteNombre: String,
    val pacienteRut: String,
    val tipoExamen: String,
    val fecha: String,
    val archivoUri: String?,
    val observaciones: String = "",
    val estado: String = "Pendiente" // Pendiente / Revisado / Autorizado
)