package com.duoc.visionsyncc1.data

import com.duoc.visionsyncc1.model.ConsultaEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.*

@Dao
interface ConsultaDao {

    @Insert
    suspend fun insertar(consulta: ConsultaEntity)

    @Query("SELECT * FROM consultas WHERE examenId = :examenId ORDER BY id DESC")
    fun observarPorExamen(examenId: Int): Flow<List<ConsultaEntity>>

    @Query("SELECT * FROM consultas ORDER BY id DESC")
    fun observarTodas(): Flow<List<ConsultaEntity>>

    @Query("SELECT COUNT(*) FROM consultas WHERE examenId = :examenId")
    suspend fun contarPorExamen(examenId: Int): Int
}