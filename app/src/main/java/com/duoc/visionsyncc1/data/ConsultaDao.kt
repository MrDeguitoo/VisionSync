package com.duoc.visionsyncc1.data

import com.duoc.visionsyncc1.model.ConsultaEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.*

@Dao
interface ConsultaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(consulta: ConsultaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(consultas: List<ConsultaEntity>)

    @Query("SELECT * FROM consultas WHERE examenId = :examenId ORDER BY fecha DESC")
    fun observarPorExamen(examenId: Int): Flow<List<ConsultaEntity>>

    @Query("SELECT * FROM consultas WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun observarPorUsuario(usuarioId: Int): Flow<List<ConsultaEntity>>

    @Query("SELECT * FROM consultas ORDER BY fecha DESC LIMIT :limit")
    fun observarUltimas(limit: Int): Flow<List<ConsultaEntity>>

    @Query("SELECT COUNT(*) FROM consultas")
    suspend fun contarTotal(): Int
}