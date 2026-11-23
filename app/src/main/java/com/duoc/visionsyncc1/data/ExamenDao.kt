package com.duoc.visionsyncc1.data

import com.duoc.visionsyncc1.model.ExamenEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.*

@Dao
interface ExamenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(examen: ExamenEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(examenes: List<ExamenEntity>)

    @Query("SELECT * FROM examenes ORDER BY id DESC")
    fun observarTodos(): Flow<List<ExamenEntity>>

    @Query("SELECT * FROM examenes WHERE estado = 'Autorizado' ORDER BY id DESC")
    fun observarAutorizados(): Flow<List<ExamenEntity>>

    @Query("SELECT * FROM examenes WHERE id = :id")
    suspend fun obtenerPorId(id: Int): ExamenEntity?

    @Query("SELECT * FROM examenes WHERE remoteId = :remoteId")
    suspend fun obtenerPorRemoteId(remoteId: Long): ExamenEntity?

    @Query("SELECT * FROM examenes WHERE syncState = 'PENDIENTE' ORDER BY id DESC")
    fun observarPendientes(): Flow<List<ExamenEntity>>

    @Query("SELECT * FROM examenes WHERE syncState = 'ERROR' ORDER BY id DESC")
    fun observarErrores(): Flow<List<ExamenEntity>>

    @Query("SELECT COUNT(*) FROM examenes WHERE syncState = 'PENDIENTE'")
    suspend fun contarPendientes(): Int

    @Query("SELECT * FROM examenes WHERE syncState = 'PENDIENTE' ORDER BY id DESC")
    suspend fun obtenerPendientesLista(): List<ExamenEntity>

    @Query("UPDATE examenes SET observaciones = :texto, estado = :estado WHERE id = :id")
    suspend fun actualizar(id: Int, texto: String, estado: String)

    // Sincronización
    @Query("UPDATE examenes SET syncState = 'SINCRONIZADO', remoteId = :remoteId, actualizadoEn = :actualizadoEn WHERE id = :localId")
    suspend fun marcarSincronizado(localId: Long, remoteId: Long, actualizadoEn: String)

    @Query("UPDATE examenes SET syncState = 'ERROR' WHERE id = :id")
    suspend fun marcarError(id: Long)

    @Query("UPDATE examenes SET documentoLocalPath = :path WHERE id = :id")
    suspend fun actualizarDocumentoLocalPath(id: Long, path: String)

    @Query("DELETE FROM examenes WHERE id = :id")
    suspend fun eliminar(id: Int)

    // Upsert para sincronización desde servidor (incluye campos extendidos)
    @Query("""
        INSERT OR REPLACE INTO examenes
        (id, pacienteNombre, pacienteRut, tipoExamen, fecha, archivoUri, observaciones, estado,
         ojoEvaluado, sucursal, profesional, fechaRealizacion, fechaValidacion, estadoExtendido,
         comentarioValidacion, comentarioEntrega,
         remoteId, syncState, documentoLocalPath, actualizadoEn)
        VALUES
        (:id, :pacienteNombre, :pacienteRut, :tipoExamen, :fecha, :archivoUri, :observaciones, :estado,
         :ojoEvaluado, :sucursal, :profesional, :fechaRealizacion, :fechaValidacion, :estadoExtendido,
         :comentarioValidacion, :comentarioEntrega,
         :remoteId, :syncState, :documentoLocalPath, :actualizadoEn)
    """)
    suspend fun upsert(
        id: Int,
        pacienteNombre: String,
        pacienteRut: String,
        tipoExamen: String,
        fecha: String,
        archivoUri: String?,
        observaciones: String,
        estado: String,
        ojoEvaluado: String,
        sucursal: String,
        profesional: String,
        fechaRealizacion: String,
        fechaValidacion: String,
        estadoExtendido: String,
        comentarioValidacion: String,
        comentarioEntrega: String,
        remoteId: Long?,
        syncState: String,
        documentoLocalPath: String?,
        actualizadoEn: String
    )

    // ===== Filtros Admin =====

    @Query("SELECT * FROM examenes WHERE estado = :estado ORDER BY id DESC")
    fun observarPorEstado(estado: String): Flow<List<ExamenEntity>>

    @Query("SELECT * FROM examenes WHERE sucursal = :sucursal ORDER BY id DESC")
    fun observarPorSucursal(sucursal: String): Flow<List<ExamenEntity>>

    @Query("SELECT * FROM examenes WHERE profesional = :profesional ORDER BY id DESC")
    fun observarPorProfesional(profesional: String): Flow<List<ExamenEntity>>

    @Query("SELECT * FROM examenes WHERE fechaRealizacion BETWEEN :inicio AND :fin ORDER BY id DESC")
    fun observarPorRangoFechas(inicio: String, fin: String): Flow<List<ExamenEntity>>

    @Query("SELECT * FROM examenes WHERE estadoExtendido = :estadoExt ORDER BY id DESC")
    fun observarPorEstadoExtendido(estadoExt: String): Flow<List<ExamenEntity>>

    @Query("SELECT DISTINCT sucursal FROM examenes WHERE sucursal IS NOT NULL AND sucursal != ''")
    suspend fun obtenerSucursales(): List<String>

    @Query("SELECT DISTINCT profesional FROM examenes WHERE profesional IS NOT NULL AND profesional != ''")
    suspend fun obtenerProfesionales(): List<String>

    // Transiciones de estado
    @Query("UPDATE examenes SET estado = :nuevoEstado, estadoExtendido = :nuevoEstadoExt, fechaValidacion = :fecha, comentarioValidacion = :comentario WHERE id = :id")
    suspend fun validarExamen(id: Int, nuevoEstado: String, nuevoEstadoExt: String, fecha: String, comentario: String)

    @Query("UPDATE examenes SET estado = 'Entregado', estadoExtendido = 'ENTREGADO', comentarioEntrega = :comentario WHERE id = :id")
    suspend fun entregarExamen(id: Int, comentario: String)

    @Query("UPDATE examenes SET estado = :nuevoEstado, estadoExtendido = :nuevoEstadoExt, comentarioValidacion = :comentario WHERE id = :id")
    suspend fun observarExamen(id: Int, nuevoEstado: String, nuevoEstadoExt: String, comentario: String)

    // Stats para dashboard admin
    @Query("SELECT COUNT(*) FROM examenes")
    suspend fun contarTotal(): Int

    @Query("SELECT COUNT(*) FROM examenes WHERE estado = 'Pendiente'")
    suspend fun contarPendientesEstado(): Int

    @Query("SELECT COUNT(*) FROM examenes WHERE estado = 'Revisado'")
    suspend fun contarRevisados(): Int

    @Query("SELECT COUNT(*) FROM examenes WHERE estado = 'Autorizado'")
    suspend fun contarAutorizados(): Int

    @Query("SELECT COUNT(*) FROM examenes WHERE estadoExtendido = 'OBSERVADO'")
    suspend fun contarObservados(): Int
}