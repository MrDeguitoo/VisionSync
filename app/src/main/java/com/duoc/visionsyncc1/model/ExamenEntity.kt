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
    val estado: String = "Pendiente", // Pendiente / Revisado / Autorizado

    // Campos de sincronización offline-first
    val remoteId: Long? = null,           // ID en el servidor (null = no sincronizado)
    val syncState: String = "PENDIENTE",  // SINCRONIZADO | PENDIENTE | ERROR
    val documentoLocalPath: String? = null, // Ruta local del documento descargado
    val actualizadoEn: String = ""        // Timestamp para resolución de conflictos
) {
    /** True si el examen está pendiente de sincronizar con el servidor */
    val isPendingSync: Boolean
        get() = syncState == "PENDIENTE"

    /** True si hubo error en la sincronización */
    val isSyncError: Boolean
        get() = syncState == "ERROR"

    /** True si está sincronizado con el servidor */
    val isSynced: Boolean
        get() = syncState == "SINCRONIZADO"
}