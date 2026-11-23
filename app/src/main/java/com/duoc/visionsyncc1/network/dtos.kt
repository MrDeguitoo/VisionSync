package com.duoc.visionsyncc1.network

import com.google.gson.annotations.SerializedName

// ==================== Auth DTOs ====================

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("rol") val rol: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("pacienteId") val pacienteId: Long?
)

// ==================== Examen DTOs ====================

data class ExamenDto(
    @SerializedName("id") val id: Long,
    @SerializedName("pacienteNombre") val pacienteNombre: String,
    @SerializedName("pacienteRut") val pacienteRut: String,
    @SerializedName("tipoExamen") val tipoExamen: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("archivoUri") val archivoUri: String?,
    @SerializedName("observaciones") val observaciones: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("pacienteId") val pacienteId: Long?,
    @SerializedName("actualizadoEn") val actualizadoEn: String
)

data class DocumentoDto(
    @SerializedName("id") val id: Long,
    @SerializedName("examenId") val examenId: Long,
    @SerializedName("nombreArchivo") val nombreArchivo: String,
    @SerializedName("tipoMime") val tipoMime: String,
    @SerializedName("urlDescarga") val urlDescarga: String
)

// ==================== FHIR DTOs ====================

data class FhirBundle(
    @SerializedName("entry") val entry: List<FhirEntry> = emptyList()
)

data class FhirEntry(
    @SerializedName("resource") val resource: FhirPatient
)

data class FhirPatient(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: List<FhirName> = emptyList(),
    @SerializedName("birthDate") val birthDate: String?
)

data class FhirName(
    @SerializedName("given") val given: List<String> = emptyList(),
    @SerializedName("family") val family: String?
)

// ==================== Mappers ====================

fun ExamenDto.toEntity(
    syncState: String = "SINCRONIZADO",
    remoteId: Long? = id,
    documentoLocalPath: String? = null,
    actualizadoEn: String = this.actualizadoEn
) = com.duoc.visionsyncc1.model.ExamenEntity(
    id = if (remoteId != null) remoteId.toInt() else 0,
    pacienteNombre = pacienteNombre,
    pacienteRut = pacienteRut,
    tipoExamen = tipoExamen,
    fecha = fecha,
    archivoUri = archivoUri,
    observaciones = observaciones,
    estado = estado,
    remoteId = remoteId,
    syncState = syncState,
    documentoLocalPath = documentoLocalPath,
    actualizadoEn = actualizadoEn
)

fun com.duoc.visionsyncc1.model.ExamenEntity.toDto(): ExamenDto = ExamenDto(
    id = remoteId ?: id.toLong(),
    pacienteNombre = pacienteNombre,
    pacienteRut = pacienteRut,
    tipoExamen = tipoExamen,
    fecha = fecha,
    archivoUri = archivoUri,
    observaciones = observaciones,
    estado = estado,
    pacienteId = null,
    actualizadoEn = actualizadoEn
)