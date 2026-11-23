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

    // Upsert para sincronización desde servidor
    @Query("""
        INSERT OR REPLACE INTO examenes
        (id, pacienteNombre, pacienteRut, tipoExamen, fecha, archivoUri, observaciones, estado,
         remoteId, syncState, documentoLocalPath, actualizadoEn)
        VALUES
        (:id, :pacienteNombre, :pacienteRut, :tipoExamen, :fecha, :archivoUri, :observaciones, :estado,
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
        remoteId: Long?,
        syncState: String,
        documentoLocalPath: String?,
        actualizadoEn: String
    )
}