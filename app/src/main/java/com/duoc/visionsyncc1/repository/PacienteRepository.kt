package com.duoc.visionsyncc1.repository

import com.duoc.visionsyncc1.data.PacienteDao
import com.duoc.visionsyncc1.model.PacienteEntity
import kotlinx.coroutines.flow.Flow

class PacienteRepository(private val dao: PacienteDao) {
    suspend fun insertar(paciente: PacienteEntity) = dao.insertar(paciente)

    fun observarTodos(): Flow<List<PacienteEntity>> = dao.observarTodos()

    fun obtenerPorRut(rut: String): Flow<PacienteEntity?> = dao.obtenerPorRut(rut)
}