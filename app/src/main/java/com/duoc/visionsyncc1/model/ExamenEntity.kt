package com.duoc.visionsyncc1.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "examenes")
data class ExamenEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pacienteNombre: String,
    val pacienteRut: String,
    val tipoExamen: String,
    val ojo: String = "", // Derecho / Izquierdo / Ambos (para formulario)
    val profesional: String = "", // Nombre del profesional (para formulario)
    val sucursal: String = "", // Sucursal (para formulario)
    val fecha: String, // Formato: yyyy-MM-dd HH:mm (para formulario)
    val archivoUri: String?,
    val descripcionAccesible: String = "", // Texto alternativo para accesibilidad (PDF/imagen)
    val observaciones: String = "",
    val estado: String = "Pendiente", // Pendiente / Revisado / Autorizado

    // Campos extendidos (admin/auditoría)
    val ojoEvaluado: String = "",       // "OD" / "OI" / "AMBOS"
    val fechaRealizacion: String = "",  // Fecha real del examen (ISO 8601)
    val fechaValidacion: String = "",   // Cuando pasó a Revisado/Autorizado
    val estadoExtendido: String = "",   // "PENDIENTE_VALIDACION" / "VALIDADO" / "ENTREGADO" / "OBSERVADO"
    val comentarioValidacion: String = "", // Comentario del médico al validar
    val comentarioEntrega: String = "",    // Comentario al entregar al paciente

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