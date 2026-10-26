package com.duoc.visionsyncc1.repository

import com.duoc.visionsyncc1.data.ExamenDao
import com.duoc.visionsyncc1.model.ExamenEntity
import kotlinx.coroutines.flow.Flow

class ExamenRepository(private val dao: ExamenDao) {
    suspend fun guardar(examen: ExamenEntity) = dao.insertar(examen)
    fun observarTodos(): Flow<List<ExamenEntity>> = dao.observarTodos()
    fun observarAutorizados(): Flow<List<ExamenEntity>> = dao.observarAutorizados()
    suspend fun obtenerPorId(id: Int): ExamenEntity? = dao.obtenerPorId(id)
    suspend fun actualizarObservaciones(id: Int, texto: String, estado: String) =
        dao.actualizar(id, texto, estado)
}