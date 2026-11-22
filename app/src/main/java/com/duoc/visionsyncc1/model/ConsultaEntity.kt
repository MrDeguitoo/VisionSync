package com.duoc.visionsyncc1.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "consultas")
data class ConsultaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examenId: Int,
    val usuarioId: Int,
    val usuarioNombre: String,
    val rol: String, // TECNOLOGO, MEDICO, PACIENTE, ADMINISTRADOR
    val fechaConsulta: String, // dd/MM/yyyy HH:mm
    val accion: String // VISUALIZAR, VALIDAR, ENTREGAR, OBSERVAR, ACTUALIZAR, DESCARGAR, EXPORTAR
)