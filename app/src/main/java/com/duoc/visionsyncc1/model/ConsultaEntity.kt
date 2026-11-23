package com.duoc.visionsyncc1.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "consultas",
    foreignKeys = [
        ForeignKey(
            entity = ExamenEntity::class,
            parentColumns = ["id"],
            childColumns = ["examenId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("examenId"),
        Index("usuarioId"),
        Index("fecha")
    ]
)
data class ConsultaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examenId: Int,
    val usuarioId: Int?,
    val accion: String, // "CREADO", "VALIDADO", "ENTREGADO", "OBSERVADO", "ACTUALIZADO", "ELIMINADO"
    val estadoAnterior: String?,
    val estadoNuevo: String,
    val comentario: String?,
    val fecha: String, // ISO 8601
    val rolUsuario: String // "TECNOLOGO", "MEDICO", "PACIENTE", "ADMINISTRADOR"
) {
    companion object {
        const val ACCION_CREADO = "CREADO"
        const val ACCION_VALIDADO = "VALIDADO"
        const val ACCION_ENTREGADO = "ENTREGADO"
        const val ACCION_OBSERVADO = "OBSERVADO"
        const val ACCION_ACTUALIZADO = "ACTUALIZADO"
        const val ACCION_ELIMINADO = "ELIMINADO"
    }
}