package com.duoc.visionsyncc1.data

import com.duoc.visionsyncc1.model.ExamenEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.*

@Dao
interface ExamenDao {

    @Insert
    suspend fun insertar(examen: ExamenEntity)

    @Query("SELECT * FROM examenes ORDER BY id DESC")
    fun observarTodos(): Flow<List<ExamenEntity>>

    @Query("SELECT * FROM examenes WHERE estado = 'Autorizado' ORDER BY id DESC")
    fun observarAutorizados(): Flow<List<ExamenEntity>>

    @Query("SELECT * FROM examenes WHERE id = :id")
    suspend fun obtenerPorId(id: Int): ExamenEntity?

    @Query("UPDATE examenes SET observaciones = :texto, estado = :estado WHERE id = :id")
    suspend fun actualizar(id: Int, texto: String, estado: String)
}