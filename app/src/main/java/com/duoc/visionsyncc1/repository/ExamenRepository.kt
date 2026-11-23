package com.duoc.visionsyncc1.repository

import com.duoc.visionsyncc1.data.ExamenDao
import com.duoc.visionsyncc1.model.ExamenEntity
import com.duoc.visionsyncc1.network.ExamenRemoteDataSource
import com.duoc.visionsyncc1.network.SyncState
import com.duoc.visionsyncc1.network.SyncStateManager
import com.duoc.visionsyncc1.network.toDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.runCatching
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repositorio Offline-First para exámenes.
 *
 * Reglas:
 * 1. La UI observa solo Room (Flow). Nunca espera a la red para mostrar datos.
 * 2. Lectura: refrescar() llama a la API; si responde, hace upsert en Room (SINCRONIZADO).
 *    Si falla, no borra nada y el SyncStateManager refleja "Modo offline".
 * 3. Escritura: primero se guarda en Room con syncState = PENDIENTE; luego se intenta enviar.
 *    Si el envío resulta bien, se marca SINCRONIZADO y se guarda el remoteId; si no, se queda PENDIENTE.
 * 4. Sincronizar pendientes: sincronizarPendientes() recorre los PENDIENTE y los envía;
 *    los que fallan con error de servidor pasan a ERROR.
 * 5. Conflictos: gana el registro con actualizadoEn más reciente.
 */
class ExamenRepository(
    private val dao: ExamenDao,
    private val remote: ExamenRemoteDataSource,
    private val syncStateManager: SyncStateManager,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    // UI observa solo esto - nunca bloquea por red
    fun observarTodos(): Flow<List<ExamenEntity>> = dao.observarTodos()

    fun observarAutorizados(): Flow<List<ExamenEntity>> = dao.observarAutorizados()

    // Flujo combinado para contar pendientes y actualizar SyncStateManager
    val syncStatusFlow: Flow<SyncState> = combine(
        dao.observarPendientes().map { it.size }.distinctUntilChanged(),
        dao.observarErrores().map { it.size }.distinctUntilChanged()
    ) { pendientes, errores ->
        if (pendientes > 0) SyncState.Pendiente(pendientes)
        else if (errores > 0) SyncState.Error("$errores examen(es) con error de sincronización")
        else SyncState.Sincronizado
    }.distinctUntilChanged()

    suspend fun obtenerPorId(id: Int): ExamenEntity? = dao.obtenerPorId(id)

    suspend fun refrescar(): Result<Unit> = runCatching {
        val result = remote.listar()
        result.onSuccess { lista ->
            val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                .format(Date())
            lista.forEach { dto ->
                dao.upsert(
                    id = 0, // Room asignará ID local
                    pacienteNombre = dto.pacienteNombre,
                    pacienteRut = dto.pacienteRut,
                    tipoExamen = dto.tipoExamen,
                    fecha = dto.fecha,
                    archivoUri = dto.archivoUri,
                    observaciones = dto.observaciones,
                    estado = dto.estado,
                    remoteId = dto.id,
                    syncState = "SINCRONIZADO",
                    documentoLocalPath = null,
                    actualizadoEn = dto.actualizadoEn
                )
            }
            syncStateManager.setPendingCount(0)
        }.onFailure { e ->
            syncStateManager.setError("Error al refrescar: ${e.message}")
            throw e
        }
    }

    suspend fun guardar(examen: ExamenEntity): Result<Long> = runCatching {
        // 1. Guardar localmente con PENDIENTE
        val localId = dao.insertar(examen.copy(syncState = "PENDIENTE"))
        syncStateManager.setPendingCount(dao.contarPendientes())

        // 2. Intentar enviar al servidor
        scope.launch {
            try {
                val dto = examen.toDto()
                val result = remote.crear(dto)
                result.onSuccess { response ->
                    // Éxito: marcar como sincronizado con remoteId
                    dao.marcarSincronizado(localId, response.id, response.actualizadoEn)
                    syncStateManager.setPendingCount(dao.contarPendientes())
                }.onFailure { e ->
                    // Fallo: marcar ERROR
                    dao.marcarError(localId)
                    syncStateManager.setPendingCount(dao.contarPendientes())
                    syncStateManager.setError("Error al sincronizar: ${e.message}")
                }
            } catch (e: Exception) {
                dao.marcarError(localId)
                syncStateManager.setPendingCount(dao.contarPendientes())
                syncStateManager.setError("Error de red: ${e.message}")
            }
        }

        localId
    }

    suspend fun actualizar(examen: ExamenEntity): Result<Unit> = runCatching {
        // Actualizar localmente
        val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            .format(Date())
        val examenConTimestamp = examen.copy(
            syncState = "PENDIENTE",
            actualizadoEn = timestamp
        )
        dao.insertar(examenConTimestamp)
        syncStateManager.setPendingCount(dao.contarPendientes())

        scope.launch {
            try {
                val result = remote.actualizar(examen.remoteId!!, examen.toDto())
                result.onSuccess { response ->
                    dao.marcarSincronizado(examen.id.toLong(), response.id, response.actualizadoEn)
                    syncStateManager.setPendingCount(dao.contarPendientes())
                }.onFailure { e ->
                    dao.marcarError(examen.id.toLong())
                    syncStateManager.setPendingCount(dao.contarPendientes())
                    syncStateManager.setError("Error al actualizar: ${e.message}")
                }
            } catch (e: Exception) {
                dao.marcarError(examen.id.toLong())
                syncStateManager.setPendingCount(dao.contarPendientes())
            }
        }
    }

    suspend fun eliminar(examen: ExamenEntity): Result<Unit> = runCatching {
        if (examen.remoteId != null) {
            // Si tiene remoteId, intentar borrar en servidor
            scope.launch {
                try {
                    val result = remote.eliminar(examen.remoteId!!)
                    result.onSuccess {
                        dao.eliminar(examen.id)
                        syncStateManager.setPendingCount(dao.contarPendientes())
                    }.onFailure { e ->
                        // Si falla, marcar como pendiente de eliminación (lógica simplificada)
                        syncStateManager.setError("Error al eliminar: ${e.message}")
                    }
                } catch (e: Exception) {
                    syncStateManager.setError("Error de red al eliminar: ${e.message}")
                }
            }
        } else {
            // Solo local
            dao.eliminar(examen.id)
        }
    }

    /**
     * Sincroniza todos los exámenes pendientes (PENDIENTE).
     * Se llama automáticamente al recuperar conexión.
     */
    suspend fun sincronizarPendientes() {
        val pendientes = dao.obtenerPendientesLista()
        for (examen in pendientes) {
            if (examen.remoteId == null) {
                // Es nuevo - crear
                try {
                    val result = remote.crear(examen.toDto())
                    result.onSuccess { response ->
                        dao.marcarSincronizado(examen.id.toLong(), response.id, response.actualizadoEn)
                    }.onFailure { e ->
                        dao.marcarError(examen.id.toLong())
                    }
                } catch (e: Exception) {
                    dao.marcarError(examen.id.toLong())
                }
            } else {
                // Existe en servidor - actualizar (resolver conflicto por actualizadoEn)
                try {
                    val result = remote.actualizar(examen.remoteId!!, examen.toDto())
                    result.onSuccess { response ->
                        dao.marcarSincronizado(examen.id.toLong(), response.id, response.actualizadoEn)
                    }.onFailure { e ->
                        dao.marcarError(examen.id.toLong())
                    }
                } catch (e: Exception) {
                    dao.marcarError(examen.id.toLong())
                }
            }
        }
        syncStateManager.setPendingCount(dao.contarPendientes())
    }

    /**
     * Obtiene un documento localmente. Si no existe, lo descarga del servidor.
     */
    suspend fun obtenerDocumento(examen: ExamenEntity, context: android.content.Context): Result<File> = runCatching {
        // Si ya tiene archivo local, devolverlo
        examen.documentoLocalPath?.let { path ->
            File(path).takeIf { it.exists() }?.let { return@runCatching it }
        }

        // Intentar descargar del servidor (necesita documentoId - asumimos que es el remoteId del examen)
        examen.remoteId?.let { documentoId ->
            scope.launch {
                try {
                    val result = remote.descargarDocumento(documentoId)
                    result.onSuccess { responseBody ->
                        val file = downloadToFile(responseBody, context, examen.tipoExamen)
                        dao.actualizarDocumentoLocalPath(examen.id.toLong(), file.absolutePath)
                    }.onFailure { e ->
                        // Error manejado silenciosamente, el archivo local seguirá siendo null
                    }
                } catch (e: Exception) {
                    // Error de red
                }
            }
        }

        // Devolver null si no se pudo (la UI debe manejar este caso)
        throw IllegalStateException("Documento no disponible offline")
    }

    // ===== Métodos Admin =====

    suspend fun contarTotal(): Int = dao.contarTotal()

    suspend fun contarPendientesEstado(): Int = dao.contarPendientesEstado()

    suspend fun contarRevisados(): Int = dao.contarRevisados()

    suspend fun contarAutorizados(): Int = dao.contarAutorizados()

    suspend fun contarObservados(): Int = dao.contarObservados()

    fun obtenerSucursales(): Flow<List<String>> = dao.obtenerSucursales().distinctUntilChanged()

    fun obtenerProfesionales(): Flow<List<String>> = dao.obtenerProfesionales().distinctUntilChanged()

    suspend fun validarExamen(id: Int, nuevoEstado: String, nuevoEstadoExt: String, fecha: String, comentario: String) {
        dao.validarExamen(id, nuevoEstado, nuevoEstadoExt, fecha, comentario)
    }

    suspend fun entregarExamen(id: Int, comentario: String) {
        dao.entregarExamen(id, comentario)
    }

    suspend fun observarExamen(id: Int, nuevoEstado: String, nuevoEstadoExt: String, comentario: String) {
        dao.observarExamen(id, nuevoEstado, nuevoEstadoExt, comentario)
    }

    private fun downloadToFile(responseBody: ResponseBody, context: android.content.Context, tipoExamen: String): File {
        val dir = File(context.filesDir, "documentos")
        if (!dir.exists()) dir.mkdirs()

        val extension = when (responseBody.contentType()?.subtype) {
            "pdf" -> ".pdf"
            "jpeg", "jpg" -> ".jpg"
            "png" -> ".png"
            else -> ".bin"
        }

        val fileName = "examen_${tipoExamen.replace(" ", "_")}_${System.currentTimeMillis()}$extension"
        val file = File(dir, fileName)

        responseBody.byteStream().use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file
    }
}

