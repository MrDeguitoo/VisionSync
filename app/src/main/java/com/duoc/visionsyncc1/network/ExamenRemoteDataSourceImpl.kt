package com.duoc.visionsyncc1.network

import kotlin.runCatching
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File

class ExamenRemoteDataSourceImpl(
    private val api: VisionSyncApi
) : ExamenRemoteDataSource {

    override suspend fun listar(): Result<List<ExamenDto>> = runCatching {
        api.examenes()
    }

    override suspend fun crear(e: ExamenDto): Result<ExamenDto> = runCatching {
        api.crearExamen(e)
    }

    override suspend fun actualizar(id: Long, e: ExamenDto): Result<ExamenDto> = runCatching {
        api.actualizarExamen(id, e)
    }

    override suspend fun eliminar(id: Long): Result<Unit> = runCatching {
        api.eliminarExamen(id)
    }

    override suspend fun subirDocumento(examenId: Long, archivo: MultipartBody.Part): Result<DocumentoDto> = runCatching {
        api.subirDocumento(examenId, archivo)
    }

    override suspend fun descargarDocumento(documentoId: Long): Result<ResponseBody> = runCatching {
        api.descargarDocumento(documentoId)
    }
}

class PacienteRemoteDataSourceImpl(
    private val api: VisionSyncApi
) : PacienteRemoteDataSource {

    override suspend fun listar(): Result<List<PacienteDto>> = runCatching {
        api.pacientes()
    }
}

/**
 * Helper para crear MultipartBody.Part desde un File
 */
fun createFilePart(file: File, fieldName: String = "archivo"): MultipartBody.Part {
    val mediaType = OkHttpHelper.parseMediaType("application/octet-stream")!!
    val requestFile = OkHttpHelper.createRequestBody(mediaType, file)
    return MultipartBody.Part.createFormData(fieldName, file.name, requestFile)
}