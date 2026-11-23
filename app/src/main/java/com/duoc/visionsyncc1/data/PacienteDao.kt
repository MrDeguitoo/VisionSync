package com.duoc.visionsyncc1.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duoc.visionsyncc1.model.PacienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PacienteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(paciente: PacienteEntity)

    @Query("SELECT * FROM pacientes ORDER BY nombre ASC")
    fun observarTodos(): Flow<List<PacienteEntity>>

    @Query("SELECT * FROM pacientes WHERE rut = :rut")
    fun obtenerPorRut(rut: String): Flow<PacienteEntity?>
}