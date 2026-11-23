package com.duoc.visionsyncc1.repository

import com.duoc.visionsyncc1.network.FhirApi
import com.duoc.visionsyncc1.network.FhirBundle
import com.duoc.visionsyncc1.network.FhirPatient
import kotlin.runCatching

/**
 * Repositorio para API externa HAPI FHIR.
 * Solo lectura, sin autenticación, datos sintéticos de prueba.
 */
class FhirRepository(
    private val api: FhirApi
) {

    suspend fun obtenerPacientes(cantidad: Int = 10): Result<List<FhirPacienteUi>> = runCatching {
        val bundle = api.pacientes(cantidad)
        bundle.entry.map { entry ->
            val patient = entry.resource
            val name = patient.name.firstOrNull()
            FhirPacienteUi(
                id = patient.id,
                nombre = name?.given?.firstOrNull() ?: "",
                apellido = name?.family ?: "",
                fechaNacimiento = patient.birthDate ?: ""
            )
        }
    }
}

data class FhirPacienteUi(
    val id: String,
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: String
) {
    val nombreCompleto: String
        get() = "$nombre $apellido".trim()
}