package com.duoc.visionsyncc1.network

import com.duoc.visionsyncc1.model.ExamenEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para el data source remoto (API).
 * Permite testing con MockK sin depender de Retrofit directamente.
 */
interface ExamenRemoteDataSource {
    suspend fun listar(): Result<List<ExamenDto>>
    suspend fun crear(e: ExamenDto): Result<ExamenDto>
    suspend fun actualizar(id: Long, e: ExamenDto): Result<ExamenDto>
    suspend fun eliminar(id: Long): Result<Unit>
    suspend fun subirDocumento(examenId: Long, archivo: okhttp3.MultipartBody.Part): Result<DocumentoDto>
    suspend fun descargarDocumento(documentoId: Long): Result<okhttp3.ResponseBody>
}

interface PacienteRemoteDataSource {
    suspend fun listar(): Result<List<PacienteDto>>
}